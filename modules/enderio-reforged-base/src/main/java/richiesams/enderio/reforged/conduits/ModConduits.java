package richiesams.enderio.reforged.conduits;

import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitRegistryUtil;

public class ModConduits {
    public static Conduit ITEM_CONDUIT;
    public static Conduit BASIC_FLUID_CONDUIT;

    public static void registerConduits() {
        EnderIOReforgedBaseMod.LOGGER.info("Registering conduits");

        ITEM_CONDUIT = ConduitRegistryUtil.registerConduit(
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "item_conduit"),
                Conduit::new,
                ItemConduitEntity::new
        );
        BASIC_FLUID_CONDUIT = ConduitRegistryUtil.registerConduit(
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "basic_fluid_conduit"),
                Conduit::new,
                BasicFluidConduitEntity::new
        );
    }
}
