package richiesams.enderio.reforged.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
        return BlockRenderType.MODEL;
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof ConduitBundleBlockEntity blockEntity) {
                blockEntity.onUse(state, world, this);
            }
        }

        return ActionResult.SUCCESS;
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
}
