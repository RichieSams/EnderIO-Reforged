package richiesams.enderio.reforged.blockentities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.blocks.ModBlocks;

public class ModBlockEntities {
    public static BlockEntityType<AlloySmelterBlockEntity> ALLOY_SMELTER;
    public static BlockEntityType<SimpleAlloySmelterBlockEntity> SIMPLE_ALLOY_SMELTER;

    public static void registerAllBlockEntities() {
        ALLOY_SMELTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "alloy_smelter"),
                FabricBlockEntityTypeBuilder.create(AlloySmelterBlockEntity::new, ModBlocks.ALLOY_SMELTER).build(null));
        SIMPLE_ALLOY_SMELTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "simple_alloy_smelter"),
                FabricBlockEntityTypeBuilder.create(SimpleAlloySmelterBlockEntity::new, ModBlocks.SIMPLE_ALLOY_SMELTER).build(null));
    }
}
