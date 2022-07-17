package richiesams.enderio.reforged;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.blocks.ModBlocks;
import richiesams.enderio.reforged.rendering.ConduitBundleModelProvider;
import richiesams.enderio.reforged.screens.AlloySmelterScreen;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.screens.SagMillScreen;
import richiesams.enderio.reforged.screens.SimpleAlloySmelterScreen;

import java.util.Map.Entry;


public class EnderIOReforgedBaseClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(providerSupplier -> new ConduitBundleModelProvider());

        HandledScreens.register(ModScreenHandlers.ALLOY_SMELTER_SCREEN_HANDLER, AlloySmelterScreen::new);
        HandledScreens.register(ModScreenHandlers.SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER, SimpleAlloySmelterScreen::new);
        HandledScreens.register(ModScreenHandlers.SAG_MILL_SCREEN_HANDLER, SagMillScreen::new);

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            EnderIOReforgedBaseMod.LOGGER.info("Registering conduit textures");
            for (Entry<RegistryKey<Conduit>, Conduit> entry : EnderIOReforgedRegistries.CONDUIT.getEntrySet()) {
                Conduit conduit = entry.getValue();
                registry.register(conduit.CoreSprite.identifier());
                registry.register(conduit.ConnectorOuterSprite.identifier());
                if (conduit.ConnectorInnerSprite != null) {
                    registry.register(conduit.ConnectorInnerSprite.identifier());
                }
            }

            // Additionally load the "crossover" texture
            registry.register(new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit/conduit_crossover"));
        }));

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONDUIT_BUNDLE, RenderLayer.getCutout());
    }
}
