package richiesams.enderio.reforged.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;
import richiesams.enderio.reforged.blockentities.ModBlockEntities;

public class ConduitBundleBlock extends BlockWithEntity implements BlockEntityProvider {
    protected ConduitBundleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // Invisible, because we render using a BlockEntityRenderer
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConduitBundleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CONDUIT_BUNDLE, ConduitBundleBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConduitBundleBlockEntity conduitBundleBlockEntity) {
            return conduitBundleBlockEntity.getOutlineShape();
        }

        return super.getOutlineShape(state, world, pos, context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConduitBundleBlockEntity conduitBundleBlockEntity) {
            return conduitBundleBlockEntity.getCollisionShape();
        }

        return super.getCollisionShape(state, world, pos, context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.getBlockEntity(pos) instanceof ConduitBundleBlockEntity blockEntity) {
            blockEntity.neighborUpdate();
        }

        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }
}
