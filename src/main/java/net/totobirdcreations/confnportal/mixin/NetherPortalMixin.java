package net.totobirdcreations.confnportal.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import net.totobirdcreations.confnportal.Mod;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;


@Mixin(NetherPortal.class)
public class NetherPortalMixin {

	private final NetherPortal self = (NetherPortal)(Object) this;

	private final ArrayList<BlockPos> blocks    = new ArrayList<>();
	private       Direction.Axis      direction = Direction.Axis.X;
	private       @Nullable World     world     = null;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(WorldAccess view, BlockPos pos, Direction.Axis axis, CallbackInfo ci) {
		this.world = view instanceof World world$ ? world$ : null;
		if (this.portalsAllowCustomShapes()) {
			self.lowerCorner = null;
			self.width       = 0;
			self.height      = 0;
			this.blocks.clear();
			this.direction = Direction.Axis.X;
			if (! this.checkAxis(pos, 0)) {
				this.blocks.clear();
				this.direction = Direction.Axis.Z;
				if (! this.checkAxis(pos, 0)) {
					this.blocks.clear();
				}
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean checkAxis(BlockPos pos, int depth) {
		assert this.world != null;
		if (this.blocks.contains(pos)) {
			return true;
		}
		if (this.isValidFrameBlock(pos)) {
			return true;
		}
		if (depth >= this.portalsCustomSearchMaxDepth()) {
			return false;
		}
		BlockState state = this.world.getBlockState(pos);
		if (state.isAir() || state.isIn(BlockTags.FIRE)) {
			this.blocks.add(pos);
			for (Direction offset : Direction.values()) {
				if (offset.getAxis() == Direction.Axis.Y || offset.rotateYClockwise().getAxis() != this.direction) {
					BlockPos next = pos.offset(offset, 1);
					if (! this.checkAxis(next, depth + 1)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
	private void isValid(CallbackInfoReturnable<Boolean> cir) {
		if (this.portalsAllowCustomShapes()) {
			int size = this.blocks.size();
			cir.setReturnValue(size > 0 && size >= this.portalsCustomShapeMinBlocks() && size <= this.portalsCustomShapeMaxBlocks());
		}
	}

	@Inject(method = "createPortal", at = @At("HEAD"), cancellable = true)
	private void createPortal(CallbackInfo ci) {
		if (this.portalsAllowCustomShapes()) {
			assert this.world != null;
			BlockState state = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.direction);
			for (BlockPos pos : this.blocks) {
				this.world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
			}
			ci.cancel();
		}
	}

	@Inject(method = "method_30487", at = @At("HEAD"), cancellable = true, remap = false)
	private static void isValidFrameBlock(BlockState state, BlockView view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (view instanceof World world && world.getGameRules().getBoolean(Mod.PORTALS_ALLOW_CUSTOM_SHAPES)) {
			cir.setReturnValue(isValidFrameBlock(state, view, pos));
		}
	}

	private static boolean isValidFrameBlock(BlockState state, BlockView view, BlockPos ignored) {
		return view instanceof World world && (
				(world.getGameRules().getBoolean(Mod.PORTALS_ALLOW_CRYING_OBSIDIAN) && state.isOf(Blocks.CRYING_OBSIDIAN))
						|| (state.isOf(Blocks.OBSIDIAN))
		);
	}

	private boolean isValidFrameBlock(BlockPos pos) {
		return this.world != null && isValidFrameBlock(this.world.getBlockState(pos), this.world, pos);
	}


	private boolean portalsAllowCustomShapes() {
		return this.world != null && this.world.getGameRules().getBoolean(Mod.PORTALS_ALLOW_CUSTOM_SHAPES);
	}

	private int portalsCustomSearchMaxDepth() {
		return this.world != null ? this.world.getGameRules().getInt(Mod.PORTALS_CUSTOM_SEARCH_MAX_DEPTH) : -1;
	}

	private int portalsCustomShapeMinBlocks() {
		return this.world != null ? this.world.getGameRules().getInt(Mod.PORTALS_CUSTOM_SHAPE_MIN_BLOCKS) : -1;
	}

	private int portalsCustomShapeMaxBlocks() {
		return this.world != null ? this.world.getGameRules().getInt(Mod.PORTALS_CUSTOM_SHAPE_MAX_BLOCKS) : -1;
	}

}