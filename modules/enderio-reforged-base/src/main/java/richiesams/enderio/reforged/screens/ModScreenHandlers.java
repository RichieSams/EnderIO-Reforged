package richiesams.enderio.reforged.screens;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class ModScreenHandlers {
    public static ScreenHandlerType<AlloySmelterScreenHandler> ALLOY_SMELTER_SCREEN_HANDLER =
            ScreenHandlerRegistry.registerSimple(new Identifier(EnderIOReforgedBaseMod.MOD_ID, "alloy_smelter"),
                    AlloySmelterScreenHandler::new);
    public static ScreenHandlerType<SimpleAlloySmelterScreenHandler> SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER =
            ScreenHandlerRegistry.registerSimple(new Identifier(EnderIOReforgedBaseMod.MOD_ID, "simple_alloy_smelter"),
                    SimpleAlloySmelterScreenHandler::new);
}
