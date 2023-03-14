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
    public static BlockEntityType<SagMillBlockEntity> SAG_MILL;
    public static BlockEntityType<StirlingGeneratorBlockEntity> STIRLING_GENERATOR;
    public static BlockEntityType<ConduitBundleBlockEntity> CONDUIT_BUNDLE;

    @SuppressWarnings("UnstableApiUsage")
    public static void registerBlockEntities() {
        ALLOY_SMELTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "alloy_smelter"),
                FabricBlockEntityTypeBuilder.create(AlloySmelterBlockEntity::new, ModBlocks.ALLOY_SMELTER).build(null));
        AlloySmelterBlockEntity.registerStorage(ALLOY_SMELTER);

        SIMPLE_ALLOY_SMELTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "simple_alloy_smelter"),
                FabricBlockEntityTypeBuilder.create(SimpleAlloySmelterBlockEntity::new, ModBlocks.SIMPLE_ALLOY_SMELTER).build(null));
        SimpleAlloySmelterBlockEntity.registerStorage(SIMPLE_ALLOY_SMELTER);

        SAG_MILL = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "sag_mill"),
                FabricBlockEntityTypeBuilder.create(SagMillBlockEntity::new, ModBlocks.SAG_MILL).build(null));
        SagMillBlockEntity.registerStorage(SAG_MILL);

        STIRLING_GENERATOR = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "stirling_generator"),
                FabricBlockEntityTypeBuilder.create(StirlingGeneratorBlockEntity::new, ModBlocks.STIRLING_GENERATOR).build(null));
        StirlingGeneratorBlockEntity.registerStorage(STIRLING_GENERATOR);

        CONDUIT_BUNDLE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit_bundle"),
                FabricBlockEntityTypeBuilder.create(ConduitBundleBlockEntity::new, ModBlocks.CONDUIT_BUNDLE).build(null));
    }
}
