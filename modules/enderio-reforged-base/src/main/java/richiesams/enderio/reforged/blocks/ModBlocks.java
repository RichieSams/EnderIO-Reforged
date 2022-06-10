package richiesams.enderio.reforged.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.items.ModItemGroup;

public class ModBlocks {
    public static final Block SIMPLE_ALLOY_SMELTER = registerBlock("simple_alloy_smelter",
            new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(6f)), ModItemGroup.ENDERIO_REFORGED);

    public static final Block ALLOY_SMELTER = registerBlock("alloy_smelter",
            new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(6f)), ModItemGroup.ENDERIO_REFORGED);

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(EnderIOReforgedBaseMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(EnderIOReforgedBaseMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static void RegisterModBlocks() {
        EnderIOReforgedBaseMod.LOGGER.info("Registering Mod Blocks for " + EnderIOReforgedBaseMod.MOD_ID + "-base");
    }
}
