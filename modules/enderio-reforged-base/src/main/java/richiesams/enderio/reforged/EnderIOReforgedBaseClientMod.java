package richiesams.enderio.reforged;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import richiesams.enderio.reforged.screens.AlloySmelterScreen;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.screens.SimpleAlloySmelterScreen;

public class EnderIOReforgedBaseClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.ALLOY_SMELTER_SCREEN_HANDLER, AlloySmelterScreen::new);
        HandledScreens.register(ModScreenHandlers.SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER, SimpleAlloySmelterScreen::new);
    }
}
