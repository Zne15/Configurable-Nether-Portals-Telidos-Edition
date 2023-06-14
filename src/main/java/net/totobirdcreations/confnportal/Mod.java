package net.totobirdcreations.confnportal;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;


public class Mod implements ModInitializer {

    public static final GameRules.Key<GameRules.BooleanRule> PORTALS_ALLOW_CRYING_OBSIDIAN   = GameRuleRegistry.register("portalsAllowCryingObsidian"  , GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> PORTALS_ALLOW_CUSTOM_SHAPES     = GameRuleRegistry.register("portalsAllowCustomShapes"    , GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.IntRule>     PORTALS_CUSTOM_SEARCH_MAX_DEPTH = GameRuleRegistry.register("portalsCustomSearchMaxDepth" , GameRules.Category.MISC, GameRuleFactory.createIntRule(512));
    public static final GameRules.Key<GameRules.IntRule>     PORTALS_CUSTOM_SHAPE_MIN_BLOCKS = GameRuleRegistry.register("portalsCustomShapeMinBlocks" , GameRules.Category.MISC, GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.IntRule>     PORTALS_CUSTOM_SHAPE_MAX_BLOCKS = GameRuleRegistry.register("portalsCustomShapeMaxBlocks" , GameRules.Category.MISC, GameRuleFactory.createIntRule(512));

    @Override
    public void onInitialize() {
    }

}
