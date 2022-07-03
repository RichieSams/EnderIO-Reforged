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
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitConnection;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;
import richiesams.enderio.reforged.api.conduits.ConduitOffset;
import richiesams.enderio.reforged.items.ConduitItem;
import richiesams.enderio.reforged.rendering.ConduitBundleRenderState;
import richiesams.enderio.reforged.rendering.ConduitMeshHelper;
import richiesams.enderio.reforged.rendering.ConduitShape;

import java.util.*;

public class ConduitBundleBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
    protected HashMap<UUID, ConduitEntity> conduitEntities;
    protected HashMap<UUID, ConduitShape> conduitShapes;

    public ConduitBundleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONDUIT_BUNDLE, pos, state);
        this.conduitEntities = new HashMap<>();
        this.conduitShapes = new HashMap<>();
    }

    public static void tick(World world, BlockPos pos, BlockState state, ConduitBundleBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        boolean markDirty = false;
        for (ConduitEntity conduit : entity.conduitEntities.values()) {
            markDirty |= conduit.tick(world, pos, state);
        }

        if (markDirty) {
            entity.markDirty();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public boolean canAddConduit(Conduit conduit) {
        for (ConduitEntity conduitEntity : conduitEntities.values()) {
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

            for (var entry : conduitEntities.entrySet()) {
                UUID uuid = entry.getKey();
                ConduitEntity conduitEntity = entry.getValue();

                Conduit backingConduit = conduitEntity.getBackingConduit();
                if (backingConduit.Group.equals(conduit.Group)) {
                    if (conduit.Tier > backingConduit.Tier) {
                        // Replace the existing conduit
                        // TODO: We'll need to do some kind of transfer of internal data
                        ConduitEntity newConduitEntity = conduit.createConduitEntity();

                        // Add it back to the user's inventory if able, or drop it
                        ItemStack oldConduitItemStack = conduitEntity.getBackingConduit().toItemStack();
                        if (!serverPlayer.getInventory().insertStack(oldConduitItemStack)) {
                            ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, oldConduitItemStack));
                        }

                        conduitEntities.put(uuid, newConduitEntity);
                        markDirty();
                        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
                        conduitEntity.markConnectionsDirty();

                        return true;
                    }

                    return false;
                }
            }

            conduitEntities.put(UUID.randomUUID(), conduit.createConduitEntity());
            markDirty();
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
            return true;
        }

        return false;
    }

    private void regenerateShapes() {
        conduitShapes.clear();

        ConduitOffset overrideOffset = null;
        if (conduitEntities.size() == 1) {
            overrideOffset = ConduitOffset.NONE;
        }

        for (Map.Entry<UUID, ConduitEntity> entry : conduitEntities.entrySet()) {
            UUID uuid = entry.getKey();
            ConduitEntity conduitEntity = entry.getValue();

            Conduit backingConduit = conduitEntity.getBackingConduit();

            HashSet<ConduitOffset> coreOffsets = new HashSet<>();
            List<Pair<Direction, Box>> connectionShapes = new ArrayList<>();
            for (var connectionEntry : conduitEntity.getConnections().entrySet()) {
                Direction direction = connectionEntry.getKey();
                ConduitConnection connection = connectionEntry.getValue();

                ConduitOffset offset;
                switch (direction) {
                    case NORTH, SOUTH -> {
                        offset = overrideOffset != null ? overrideOffset : backingConduit.ZOffset;
                    }
                    case EAST, WEST -> {
                        offset = overrideOffset != null ? overrideOffset : backingConduit.XOffset;
                    }
                    case UP, DOWN -> {
                        offset = overrideOffset != null ? overrideOffset : backingConduit.YOffset;
                    }
                    default -> {
                        throw new RuntimeException("Invalid Direction %s".formatted(direction));
                    }
                }

                coreOffsets.add(offset);
                connectionShapes.add(new Pair<>(
                        direction,
                        ConduitMeshHelper.ConnectorFromOffset(offset, direction)
                ));
            }
            if (coreOffsets.size() == 0) {
                coreOffsets.add(overrideOffset != null ? overrideOffset : backingConduit.ZOffset);
            }

            List<Box> coreShapes = coreOffsets.stream().map((ConduitMeshHelper::CoreFromOffset)).toList();

            conduitShapes.put(uuid, new ConduitShape(coreShapes, connectionShapes));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        HashMap<UUID, ConduitEntity> newConduits = new HashMap<>();
        NbtCompound conduits = nbt.getCompound("Conduits");
        for (String uuid : conduits.getKeys()) {
            ConduitEntity entity = ConduitEntity.fromNBT(conduits.getCompound(uuid));
            newConduits.put(UUID.fromString(uuid), entity);
        }

        conduitEntities = newConduits;

        // TODO: Only trigger block update if the model actually changed
        if (getWorld() != null && getWorld().isClient) {
            // Trigger the model to be rebuilt
            regenerateShapes();
            MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtCompound conduits = new NbtCompound();
        for (var entry : conduitEntities.entrySet()) {
            NbtCompound compound = new NbtCompound();
            entry.getValue().writeNbt(compound);
            conduits.put(entry.getKey().toString(), compound);
        }
        nbt.put("Conduits", conduits);

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
        List<ConduitBundleRenderState.ConduitRenderState> renderStates =
                conduitEntities.entrySet().stream().map(
                        (Map.Entry<UUID, ConduitEntity> entry) -> new ConduitBundleRenderState.ConduitRenderState(
                                entry.getValue().getBackingConduit(),
                                conduitShapes.get(entry.getKey())
                        )
                ).toList();

        return new ConduitBundleRenderState(renderStates);
    }

    public void neighborUpdate() {
        for (ConduitEntity conduitEntity : conduitEntities.values()) {
            conduitEntity.markConnectionsDirty();
        }
    }

    public VoxelShape getOutlineShape() {
        // TODO: We should probably cache this and only update when the state changes
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        ArrayList<VoxelShape> shapes = new ArrayList<>();
        if (conduitEntities.size() == 0) {
            // Fail-safe
            return VoxelShapes.fullCube();
        }

        for (ConduitShape conduitShape : conduitShapes.values()) {
            for (Box box : conduitShape.cores()) {
                shapes.add(VoxelShapes.cuboid(box));
            }
            for (Pair<Direction, Box> pair : conduitShape.connections()) {
                shapes.add(VoxelShapes.cuboid(pair.getRight()));
            }
        }

        return shapes.stream().reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR))
                .orElseGet(VoxelShapes::fullCube);
    }
}
