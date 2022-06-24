package richiesams.enderio.reforged.blockentities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;
import richiesams.enderio.reforged.blocks.ConduitBundleBlock;
import richiesams.enderio.reforged.conduits.ModConduits;
import richiesams.enderio.reforged.rendering.ConduitBundleRenderState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConduitBundleBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
    protected List<ConduitEntity> conduits;

    public ConduitBundleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONDUIT_BUNDLE, pos, state);
        this.conduits = Arrays.asList(ModConduits.ITEM_CONDUIT.createConduitEntity());
    }

    public static void tick(World world, BlockPos pos, BlockState state, ConduitBundleBlockEntity entity) {
        for (ConduitEntity conduit : entity.conduits) {
            conduit.tick(world, pos, state);
        }
    }

    public void onUse(BlockState currentState, World world, ConduitBundleBlock block) {

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        ArrayList<ConduitEntity> newConduits = new ArrayList<>();
        NbtList conduitList = nbt.getList("Conduits", 10);
        for (int i = 0; i < conduitList.size(); ++i) {
            ConduitEntity entity = ConduitEntity.fromNBT(conduitList.getCompound(i));
            if (entity != null) {
                newConduits.add(entity);
            }
        }
        conduits = newConduits;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (ConduitEntity entity : conduits) {
            NbtCompound compound = new NbtCompound();
            entity.writeNbt(compound);
            nbtList.add(compound);
        }
        nbt.put("Conduits", nbtList);

        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Object getRenderAttachmentData() {
        List<Conduit> backingConduits = conduits.stream().map((ConduitEntity::getBackingConduit)).toList();

        // TODO: Add connections
        return new ConduitBundleRenderState(backingConduits, new ArrayList<>());
    }

    public VoxelShape getOutlineShape() {
        // TODO: We should probably cache this and only update when the state changes
        ArrayList<VoxelShape> shapes = new ArrayList<>();
        if (conduits.size() == 1) {
            shapes.add(VoxelShapes.cuboid(
                    6.5f / 16.0f, 6.5f / 16.0f, 6.5f / 16.0f,
                    9.5f / 16.0f, 9.5f / 16.0f, 9.5f / 16.0f));
        } else {

        }

        // TODO: Connections

        return shapes.stream().reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR))
                .orElseGet(VoxelShapes::fullCube);
    }
}
