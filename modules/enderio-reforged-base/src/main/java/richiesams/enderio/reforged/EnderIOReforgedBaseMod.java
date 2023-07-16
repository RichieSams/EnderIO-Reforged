package richiesams.enderio.reforged;

import net.fabricmc.api.ModInitializer;
import net.minecraft.ExtendedVanilla;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richiesams.enderio.reforged.blockentities.ModBlockEntities;
import richiesams.enderio.reforged.blocks.ModBlocks;
import richiesams.enderio.reforged.conduits.ModConduits;
import richiesams.enderio.reforged.events.ModEventHandlers;
import richiesams.enderio.reforged.items.ModItems;
import richiesams.enderio.reforged.recipes.ModRecipes;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.util.ModTags;

public class EnderIOReforgedBaseMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "enderio-reforged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello world from EnderIO Reforged Base");

        ExtendedVanilla.onInitialize();

        ModBlocks.registerBlocks();
        ModBlockEntities.registerBlockEntities();
        ModConduits.registerConduits();
        ModItems.registerItems();
        ModScreenHandlers.registerScreenHandlers();
        ModTags.Items.registerItemTags();
        ModTags.Blocks.registerBlockTags();
        ModRecipes.registerRecipes();

        ModEventHandlers.registerEventHandlers();
    }
}