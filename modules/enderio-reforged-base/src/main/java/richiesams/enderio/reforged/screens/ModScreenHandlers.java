package richiesams.enderio.reforged.screens;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class ModScreenHandlers {
    public static ExtendedScreenHandlerType<BuiltScreenHandler> ALLOY_SMELTER_SCREEN_HANDLER;
    public static ExtendedScreenHandlerType<BuiltScreenHandler> SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER;

    private static BuiltScreenHandler create(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        final BlockEntity blockEntity = inventory.player.world.getBlockEntity(buf.readBlockPos());
        return ((BuiltScreenHandlerProvider) blockEntity).createScreenHandler(syncId, inventory.player);
    }

    public static void registerScreenHandlers() {
        ALLOY_SMELTER_SCREEN_HANDLER = Registry.register(Registry.SCREEN_HANDLER,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "alloy_smelter"),
                new ExtendedScreenHandlerType<>(ModScreenHandlers::create));
        SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER = Registry.register(Registry.SCREEN_HANDLER,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "simple_alloy_smelter"),
                new ExtendedScreenHandlerType<>(ModScreenHandlers::create));
    }
}
