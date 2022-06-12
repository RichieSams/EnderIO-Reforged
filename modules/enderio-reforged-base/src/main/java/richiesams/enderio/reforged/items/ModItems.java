package richiesams.enderio.reforged.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;


public class ModItems {
    public static Item YETA_WRENCH = registerItem("yeta_wrench",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item DARK_STEEL_INGOT = registerItem("dark_steel_ingot",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item DARK_STEEL_NUGGET = registerItem("dark_steel_nugget",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item WOOD_GEAR = registerItem("wood_gear",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item STONE_GEAR = registerItem("stone_gear",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item IRON_GEAR = registerItem("iron_gear",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item DARK_STEEL_GEAR = registerItem("dark_steel_gear",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item INFINITY_POWDER = registerItem("infinity_powder",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item SIMPLE_MACHINE_CHASSIS = registerItem("simple_machine_chassis",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));
    public static Item INDUSTRIAL_MACHINE_CHASSIS = registerItem("industrial_machine_chassis",
            new Item(new FabricItemSettings().group(ModItemGroup.ENDERIO_REFORGED)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(EnderIOReforgedBaseMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        EnderIOReforgedBaseMod.LOGGER.info("Registering Mod Items for " + EnderIOReforgedBaseMod.MOD_ID + "-base");
    }
}
