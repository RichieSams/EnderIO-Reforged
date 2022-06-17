package richiesams.enderio.reforged.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.items.ModItemGroup;

public class ModBlocks {
    public static Block ALLOY_SMELTER;
    public static Block SIMPLE_ALLOY_SMELTER;


    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(EnderIOReforgedBaseMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block, ItemGroup group) {
        Registry.register(Registry.ITEM, new Identifier(EnderIOReforgedBaseMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static void registerBlocks() {
        ALLOY_SMELTER = registerBlock("alloy_smelter",
                new AlloySmelterBlock(FabricBlockSettings.of(Material.METAL).strength(6f)), ModItemGroup.ENDERIO_REFORGED);
        SIMPLE_ALLOY_SMELTER = registerBlock("simple_alloy_smelter",
                new SimpleAlloySmelterBlock(FabricBlockSettings.of(Material.METAL).strength(6f)), ModItemGroup.ENDERIO_REFORGED);
    }
}
