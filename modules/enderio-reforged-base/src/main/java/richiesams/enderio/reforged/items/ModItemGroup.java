package richiesams.enderio.reforged.items;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.blocks.ModBlocks;

public class ModItemGroup {
    public static ItemGroup ENDERIO_REFORGED = FabricItemGroupBuilder.build(new Identifier(EnderIOReforgedBaseMod.MOD_ID, "item_group"),
            () -> new ItemStack(ModBlocks.ALLOY_SMELTER));
}
