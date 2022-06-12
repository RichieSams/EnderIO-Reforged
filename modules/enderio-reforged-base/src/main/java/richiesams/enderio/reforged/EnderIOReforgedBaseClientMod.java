package richiesams.enderio.reforged;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import richiesams.enderio.reforged.screens.AlloySmelterScreen;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.screens.SimpleAlloySmelterScreen;

public class EnderIOReforgedBaseClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(ModScreenHandlers.ALLOY_SMELTER_SCREEN_HANDLER, AlloySmelterScreen::new);
        ScreenRegistry.register(ModScreenHandlers.SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER, SimpleAlloySmelterScreen::new);
    }
}
