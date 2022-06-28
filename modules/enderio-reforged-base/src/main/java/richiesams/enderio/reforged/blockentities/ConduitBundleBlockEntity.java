package richiesams.enderio.reforged.blockentities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;
import richiesams.enderio.reforged.api.conduits.ConduitOffset;
import richiesams.enderio.reforged.items.ConduitItem;
import richiesams.enderio.reforged.rendering.ConduitBundleRenderState;
import richiesams.enderio.reforged.rendering.ConduitMeshHelper;

import java.util.ArrayList;
import java.util.List;

public class ConduitBundleBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
    protected List<ConduitEntity> conduits;

    public ConduitBundleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONDUIT_BUNDLE, pos, state);
        this.conduits = new ArrayList<>();
    }

    public static void tick(World world, BlockPos pos, BlockState state, ConduitBundleBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        for (ConduitEntity conduit : entity.conduits) {
            conduit.tick(world, pos, state);
        }
    }

    public boolean canAddConduit(Conduit conduit) {
        for (ConduitEntity conduitEntity : conduits) {
            Conduit backingConduit = conduitEntity.getBackingConduit();
            if (backingConduit.Group.equals(conduit.Group)) {
                return conduit.Tier > backingConduit.Tier;
            }
        }

        return true;
    }

    public boolean addConduit(ServerPlayerEntity serverPlayer, Hand hand) {
        ItemStack stack = serverPlayer.getStackInHand(hand);
        if (stack.getItem() instanceof ConduitItem conduitItem) {
            Conduit conduit = conduitItem.getConduit();

            for (ConduitEntity conduitEntity : conduits) {
                Conduit backingConduit = conduitEntity.getBackingConduit();
                if (backingConduit.Group.equals(conduit.Group)) {
                    if (conduit.Tier > backingConduit.Tier) {
                        // Replace the existing conduit
                        conduits.remove(conduitEntity);
                        // Add it back to the user's inventory if able, or drop it
                        ItemStack oldConduitItemStack = conduitEntity.getBackingConduit().toItemStack();
                        if (!serverPlayer.getInventory().insertStack(oldConduitItemStack)) {
                            ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, oldConduitItemStack));
                        }

                        conduits.add(conduit.createConduitEntity());
                        markDirty();
                        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);

                        return true;
                    }

                    return false;
                }
            }

            conduits.add(conduit.createConduitEntity());
            markDirty();
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
            return true;
        }

        return false;
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

        if (getWorld() != null && getWorld().isClient) {
            // Trigger the model to be rebuilt
            MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
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
        if (conduits.size() == 0) {
            // Fail-safe
            return VoxelShapes.fullCube();
        } else if (conduits.size() == 1) {
            Box cube = ConduitMeshHelper.CoreFromOffset(ConduitOffset.NONE);
            shapes.add(VoxelShapes.cuboid(cube));
        } else {
            for (ConduitEntity conduit : conduits) {
                Box cube = ConduitMeshHelper.CoreFromOffset(conduit.getBackingConduit().XOffset);

                shapes.add(VoxelShapes.cuboid(cube));
            }
        }

        // TODO: Connections

        return shapes.stream().reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR))
                .orElseGet(VoxelShapes::fullCube);
    }
}
