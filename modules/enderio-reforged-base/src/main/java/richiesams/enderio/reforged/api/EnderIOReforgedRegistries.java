package richiesams.enderio.reforged.api;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.grinding_balls.GrindingBall;

public class EnderIOReforgedRegistries {
    public static final SimpleRegistry<Conduit> CONDUIT = FabricRegistryBuilder.createSimple(Conduit.class,
                    new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit_registry"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    public static final SimpleRegistry<GrindingBall> GRINDING_BALL = FabricRegistryBuilder.createSimple(GrindingBall.class,
                    new Identifier(EnderIOReforgedBaseMod.MOD_ID, "grinding_ball_registry"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
}
