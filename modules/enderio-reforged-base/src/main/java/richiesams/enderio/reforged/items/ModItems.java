package richiesams.enderio.reforged.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.blocks.ModBlocks;


public class ModItems {
    public static Item YETA_WRENCH;
    public static Item COAL_POWDER;
    public static Item DARK_STEEL_INGOT;
    public static Item ENERGETIC_ALLOY_INGOT;
    public static Item VIBRANT_ALLOY_INGOT;
    public static Item DARK_STEEL_NUGGET;
    public static Item BASIC_CAPACITOR;
    public static Item DOUBLE_LAYER_CAPACITOR;
    public static Item OCTADIC_CAPACITOR;
    public static Item WOOD_GEAR;
    public static Item STONE_GEAR;
    public static Item IRON_GEAR;
    public static Item DARK_STEEL_GEAR;
    public static Item INFINITY_POWDER;
    public static Item SIMPLE_MACHINE_CHASSIS;
    public static Item INDUSTRIAL_MACHINE_CHASSIS;
    public static Item ITEM_CONDUIT;
    public static Item VANILLA_FIRE;

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(EnderIOReforgedBaseMod.MOD_ID, name), item);
    }

    public static void registerItems() {
        YETA_WRENCH = registerItem("yeta_wrench",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        COAL_POWDER = registerItem("coal_powder",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        DARK_STEEL_INGOT = registerItem("dark_steel_ingot",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        ENERGETIC_ALLOY_INGOT = registerItem("energetic_alloy_ingot",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        VIBRANT_ALLOY_INGOT = registerItem("vibrant_alloy_ingot",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        DARK_STEEL_NUGGET = registerItem("dark_steel_nugget",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        BASIC_CAPACITOR = registerItem("basic_capacitor",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        DOUBLE_LAYER_CAPACITOR = registerItem("double_layer_capacitor",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        OCTADIC_CAPACITOR = registerItem("octadic_capacitor",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        WOOD_GEAR = registerItem("wood_gear",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        STONE_GEAR = registerItem("stone_gear",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        IRON_GEAR = registerItem("iron_gear",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        DARK_STEEL_GEAR = registerItem("dark_steel_gear",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        INFINITY_POWDER = registerItem("infinity_powder",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        SIMPLE_MACHINE_CHASSIS = registerItem("simple_machine_chassis",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        INDUSTRIAL_MACHINE_CHASSIS = registerItem("industrial_machine_chassis",
                new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
        ITEM_CONDUIT = registerItem("item_conduit",
                new BlockItem(ModBlocks.CONDUIT_BUNDLE, new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));

        VANILLA_FIRE = Registry.register(Registry.ITEM, new Identifier(EnderIOReforgedBaseMod.MOD_ID, "fire"),
                new BlockItem(Blocks.FIRE, new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    }
}
