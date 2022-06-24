package richiesams.enderio.reforged.blockentities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.blocks.ModBlocks;
import team.reborn.energy.api.EnergyStorage;

public class ModBlockEntities {
    public static BlockEntityType<AlloySmelterBlockEntity> ALLOY_SMELTER;
    public static BlockEntityType<SimpleAlloySmelterBlockEntity> SIMPLE_ALLOY_SMELTER;
    public static BlockEntityType<ConduitBundleBlockEntity> CONDUIT_BUNDLE;

    @SuppressWarnings("UnstableApiUsage")
    public static void registerBlockEntities() {
        ALLOY_SMELTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "alloy_smelter"),
                FabricBlockEntityTypeBuilder.create(AlloySmelterBlockEntity::new, ModBlocks.ALLOY_SMELTER).build(null));
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.InputStorage, ALLOY_SMELTER);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.OutputStorage, ALLOY_SMELTER);
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.EnergyStorage, ALLOY_SMELTER);

        SIMPLE_ALLOY_SMELTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "simple_alloy_smelter"),
                FabricBlockEntityTypeBuilder.create(SimpleAlloySmelterBlockEntity::new, ModBlocks.SIMPLE_ALLOY_SMELTER).build(null));

        CONDUIT_BUNDLE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit_bundle"),
                FabricBlockEntityTypeBuilder.create(ConduitBundleBlockEntity::new, ModBlocks.CONDUIT_BUNDLE).build(null));
    }
}
