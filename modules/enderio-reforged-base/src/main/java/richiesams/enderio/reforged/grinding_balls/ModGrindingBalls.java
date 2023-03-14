package richiesams.enderio.reforged.grinding_balls;

import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.grinding_balls.GrindingBall;
import richiesams.enderio.reforged.api.grinding_balls.GrindingBallRegistryUtil;

public class ModGrindingBalls {
    public static GrindingBall FLINT;
    public static GrindingBall DARK_STEEL;

    public static void registerGrindingBalls() {
        EnderIOReforgedBaseMod.LOGGER.info("Registering grinding balls");

        FLINT = GrindingBallRegistryUtil.registerGrindingBall(new Identifier("minecraft", "flint"));
        DARK_STEEL = GrindingBallRegistryUtil.registerGrindingBall(new Identifier(EnderIOReforgedBaseMod.MOD_ID, "dark_steel"));
    }
}
