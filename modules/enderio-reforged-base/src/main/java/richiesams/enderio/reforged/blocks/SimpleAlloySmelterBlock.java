package richiesams.enderio.reforged.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.blockentities.ModBlockEntities;
import richiesams.enderio.reforged.blockentities.SimpleAlloySmelterBlockEntity;

public class SimpleAlloySmelterBlock extends MachineBlock {
    public SimpleAlloySmelterBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleAlloySmelterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.SIMPLE_ALLOY_SMELTER, SimpleAlloySmelterBlockEntity::tick);
    }
}
