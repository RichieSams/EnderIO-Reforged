package richiesams.enderio.reforged.blockentities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.api.conduits.*;
import richiesams.enderio.reforged.items.ConduitItem;
import richiesams.enderio.reforged.rendering.ConduitMeshHelper;
import richiesams.enderio.reforged.rendering.ConduitShape;

import java.util.*;

public class ConduitBundleBlockEntity extends BlockEntity {
    private static final double coreHitboxExpansion = 0.25 / 16.0;
    private static final double connectionHitboxExpansion = 0.75 / 16.0;

    protected HashMap<UUID, ConduitEntity> conduitEntities;
    protected List<ConduitShape> conduitShapes;

    public ConduitBundleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONDUIT_BUNDLE, pos, state);
        this.conduitEntities = new HashMap<>();
        this.conduitShapes = new ArrayList<>();
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
                        ConduitEntity newConduitEntity = conduit.createConduitEntity(this);

                        // Add it back to the user's inventory if able, or drop it
                        ItemStack oldConduitItemStack = conduitEntity.getBackingConduit().toItemStack();
                        if (!serverPlayer.getInventory().insertStack(oldConduitItemStack)) {
                            ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, oldConduitItemStack));
                        }

                        conduitEntities.put(uuid, newConduitEntity);
                        markDirty();
                        conduitEntity.markConnectionsDirty();

                        return true;
                    }

                    return false;
                }
            }

            conduitEntities.put(UUID.randomUUID(), conduit.createConduitEntity(this));
            markDirty();
            return true;
        }

        return false;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
        regenerateShapes();
    }

    @Nullable
    public <T extends ConduitEntity> T getConduitOfType(Class<T> entityClass) {
        for (var entry : conduitEntities.entrySet()) {
            ConduitEntity entity = entry.getValue();
            if (entityClass.isInstance(entity)) {
                return entityClass.cast(entity);
            }
        }

        return null;
    }

    @Environment(EnvType.CLIENT)
    public void render(Map<Identifier, Sprite> spriteMap, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();

        for (var shape : conduitShapes) {
            Conduit backingConduit = shape.entity().getBackingConduit();

            // First render the cores
            for (var core : shape.cores()) {
                renderCuboid(vertexConsumer, spriteMap, positionMatrix, core, backingConduit.CoreSprite);
            }

            // Then render the connectors

            // Render the inner texture first, if it exists
            ArrayList<SpriteReference> connectorSpriteRefs = new ArrayList<>();
            if (backingConduit.ConnectorInnerSprite != null) {
                connectorSpriteRefs.add(backingConduit.ConnectorInnerSprite);
            }
            connectorSpriteRefs.add(backingConduit.ConnectorOuterSprite);

            for (var spriteRef : connectorSpriteRefs) {
                Sprite sprite = spriteMap.get(spriteRef.identifier());

                for (var connectionPair : shape.connections()) {
                    Direction direction = connectionPair.getLeft();
                    Box box = connectionPair.getRight();

                    switch (direction) {
                        case UP -> {
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                        }
                        case DOWN -> {
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                        }
                        case NORTH -> {
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                        }
                        case SOUTH -> {
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                        }
                        case EAST -> {
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                        }
                        case WEST -> {
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                            renderCuboidFace(vertexConsumer, positionMatrix, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                        }
                    }
                }
            }
        }




        matrices.pop();
    }


    private void renderCuboid(VertexConsumer vertexConsumer, Map<Identifier, Sprite> spriteMap, Matrix4f positionMatrix, Box cube, SpriteReference spriteRef) {
        Sprite sprite = spriteMap.get(spriteRef.identifier());

        // Render all the faces
        for (Direction direction : Direction.values()) {
            renderCuboidFace(vertexConsumer, positionMatrix, direction, cube, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
        }
    }

    private void renderCuboidFace(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Direction direction, Box cube, Sprite sprite, Vec2f uvFrom, Vec2f uvTo, Rotation uvRotation) {
        switch (direction) {
            case DOWN -> {
                renderQuad(vertexConsumer, positionMatrix, Direction.DOWN,
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.minZ),
                        sprite,
                        uvFrom,
                        uvTo,
                        uvRotation
                );
            }
            case UP -> {
                renderQuad(vertexConsumer, positionMatrix, Direction.UP,
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.maxZ),
                        sprite,
                        uvFrom,
                        uvTo,
                        uvRotation
                );
            }
            case NORTH -> {
                renderQuad(vertexConsumer, positionMatrix, Direction.NORTH,
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.minZ),
                        sprite,
                        uvFrom,
                        uvTo,
                        uvRotation
                );
            }
            case SOUTH -> {
                renderQuad(vertexConsumer, positionMatrix, Direction.SOUTH,
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ),
                        sprite,
                        uvFrom,
                        uvTo,
                        uvRotation
                );
            }
            case WEST -> {
                renderQuad(vertexConsumer, positionMatrix, Direction.WEST,
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.maxZ),
                        sprite,
                        uvFrom,
                        uvTo,
                        uvRotation
                );
            }
            case EAST -> {
                renderQuad(vertexConsumer, positionMatrix, Direction.EAST,
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.minZ),
                        sprite,
                        uvFrom,
                        uvTo,
                        uvRotation
                );
            }
        }
    }

    private void renderQuad(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Direction nominalDirection,
                            Vec3f vertex0, Vec3f vertex1, Vec3f vertex2, Vec3f vertex3,
                            Sprite sprite, Vec2f uvFrom, Vec2f uvTo, Rotation uvRotation) {
        // Shift the UV range to fit within the range of the sprite within the atlas
        final float uMin = sprite.getMinU();
        final float uSpan = sprite.getMaxU() - uMin;
        final float vMin = sprite.getMinV();
        final float vSpan = sprite.getMaxV() - vMin;

        // Vertex 0
        Vec2f uv0 = getSpriteUV(0, uvFrom, uvTo, uvRotation);
        vertexConsumer.vertex(positionMatrix, vertex0.getX(), vertex0.getY(), vertex0.getZ())
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .texture(uMin+ uv0.x * uSpan, vMin + uv0.y * vSpan)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(nominalDirection.getOffsetX(), nominalDirection.getOffsetY(), nominalDirection.getOffsetZ())
                .next();

        // Vertex 1
        Vec2f uv1 = getSpriteUV(1, uvFrom, uvTo, uvRotation);
        vertexConsumer.vertex(positionMatrix, vertex1.getX(), vertex1.getY(), vertex1.getZ())
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .texture(uMin+ uv1.x * uSpan, vMin + uv1.y * vSpan)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(nominalDirection.getOffsetX(), nominalDirection.getOffsetY(), nominalDirection.getOffsetZ())
                .next();

        // Vertex 2
        Vec2f uv2 = getSpriteUV(2, uvFrom, uvTo, uvRotation);
        vertexConsumer.vertex(positionMatrix, vertex2.getX(), vertex2.getY(), vertex2.getZ())
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .texture(uMin+ uv2.x * uSpan, vMin + uv2.y * vSpan)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(nominalDirection.getOffsetX(), nominalDirection.getOffsetY(), nominalDirection.getOffsetZ())
                .next();

        // Vertex 3
        Vec2f uv3 = getSpriteUV(3, uvFrom, uvTo, uvRotation);
        vertexConsumer.vertex(positionMatrix, vertex3.getX(), vertex3.getY(), vertex3.getZ())
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .texture(uMin+ uv3.x * uSpan, vMin + uv3.y * vSpan)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(nominalDirection.getOffsetX(), nominalDirection.getOffsetY(), nominalDirection.getOffsetZ())
                .next();
    }

    private Vec2f getSpriteUV(int vertex, Vec2f uvFrom, Vec2f uvTo, Rotation rotation) {
        Vec2f uv0 = new Vec2f(uvFrom.x, uvFrom.y);
        Vec2f uv1 = new Vec2f(uvFrom.x, uvTo.y);
        Vec2f uv2 = new Vec2f(uvTo.x, uvTo.y);
        Vec2f uv3 = new Vec2f(uvTo.x, uvFrom.y);

        switch (rotation) {
            case DEGREES_0 -> {
                return switch (vertex) {
                    case 0 -> uv0;
                    case 1 -> uv1;
                    case 2 -> uv2;
                    case 3 -> uv3;
                    default -> throw new RuntimeException("Invalid vertex index");
                };
            }
            case DEGREES_90 -> {
                switch (vertex) {
                    case 0 -> {
                        return new Vec2f(uv1.x, uv1.y);
                    }
                    case 1 -> {
                        return new Vec2f(uv2.x, uv2.y);
                    }
                    case 2 -> {
                        return new Vec2f(uv3.x, uv3.y);
                    }
                    case 3 -> {
                        return new Vec2f(uv0.x, uv0.y);
                    }
                    default -> throw new RuntimeException("Invalid vertex index");
                }
            }
            case DEGREES_180 -> {
                switch (vertex) {
                    case 0 -> {
                        return new Vec2f(uv2.x, uv2.y);
                    }
                    case 1 -> {
                        return new Vec2f(uv3.x, uv3.y);
                    }
                    case 2 -> {
                        return new Vec2f(uv0.x, uv0.y);
                    }
                    case 3 -> {
                        return new Vec2f(uv1.x, uv1.y);
                    }
                    default -> throw new RuntimeException("Invalid vertex index");
                }
            }
            case DEGREES_270 -> {
                switch (vertex) {
                    case 0 -> {
                        return new Vec2f(uv3.x, uv3.y);
                    }
                    case 1 -> {
                        return new Vec2f(uv0.x, uv0.y);
                    }
                    case 2 -> {
                        return new Vec2f(uv1.x, uv1.y);
                    }
                    case 3 -> {
                        return new Vec2f(uv2.x, uv2.y);
                    }
                    default -> throw new RuntimeException("Invalid vertex index");
                }
            }
            default -> throw new RuntimeException("Invalid rotation");
        }
    }

    public enum Rotation {
        DEGREES_0,
        DEGREES_90,
        DEGREES_180,
        DEGREES_270
    }

    private void regenerateShapes() {
        List<ConduitShape> newConduitShapes = new ArrayList<>();

        ConduitOffset overrideOffset = null;
        if (conduitEntities.size() == 1) {
            overrideOffset = ConduitOffset.NONE;
        }

        for (Map.Entry<UUID, ConduitEntity> entry : conduitEntities.entrySet()) {
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
                        offset = overrideOffset != null ? overrideOffset : backingConduit.NorthSouthOffset;
                    }
                    case EAST, WEST -> {
                        offset = overrideOffset != null ? overrideOffset : backingConduit.EastWestOffset;
                    }
                    case UP, DOWN -> {
                        offset = overrideOffset != null ? overrideOffset : backingConduit.UpDownOffset;
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
                coreOffsets.add(overrideOffset != null ? overrideOffset : backingConduit.NorthSouthOffset);
            }

            List<Box> coreShapes = coreOffsets.stream().map((ConduitMeshHelper::CoreFromOffset)).toList();

            newConduitShapes.add(new ConduitShape(conduitEntity, coreShapes, connectionShapes));
        }

        conduitShapes = newConduitShapes;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        HashMap<UUID, ConduitEntity> newConduits = new HashMap<>();
        NbtCompound conduits = nbt.getCompound("Conduits");
        for (String uuid : conduits.getKeys()) {
            ConduitEntity entity = ConduitEntity.fromNBT(this, conduits.getCompound(uuid));
            newConduits.put(UUID.fromString(uuid), entity);
        }

        conduitEntities = newConduits;
        regenerateShapes();
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

    public void neighborUpdate() {
        for (ConduitEntity conduitEntity : conduitEntities.values()) {
            conduitEntity.markConnectionsDirty();
        }
    }

    public VoxelShape getOutlineShape() {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        if (hit instanceof BlockHitResult blockHit) {
            if (world.getBlockEntity(blockHit.getBlockPos()) instanceof ConduitBundleBlockEntity blockEntity) {
                // Fail safe
                if (blockEntity.conduitEntities.size() == 0 || blockEntity.conduitShapes.size() == 0) {
                    return VoxelShapes.empty();
                }

                Vec3d hitPos = blockHit.getPos();
                hitPos = hitPos.subtract(Vec3d.of(blockHit.getBlockPos()));

                switch (blockHit.getSide()) {
                    case DOWN -> {
                        hitPos = hitPos.add(0.0, 0.01, 0.0);
                    }
                    case UP -> {
                        hitPos = hitPos.add(0.0, -0.01, 0.0);
                    }
                    case NORTH -> {
                        hitPos = hitPos.add(0.0, 0.0, 0.01);
                    }
                    case SOUTH -> {
                        hitPos = hitPos.add(0.0, 0.0, -0.01);
                    }
                    case WEST -> {
                        hitPos = hitPos.add(0.01, 0.0, 0.0);
                    }
                    case EAST -> {
                        hitPos = hitPos.add(-0.01, 0.0, 0.0);
                    }
                }

                for (ConduitShape conduitShape : blockEntity.conduitShapes) {
                    for (Box box : conduitShape.cores()) {
                        Box expandedBox = box.expand(coreHitboxExpansion);

                        if (expandedBox.contains(hitPos)) {
                            return VoxelShapes.cuboid(expandedBox);
                        }
                    }
                    for (Pair<Direction, Box> pair : conduitShape.connections()) {
                        Box box;
                        switch (pair.getLeft()) {
                            case UP, DOWN -> {
                                box = pair.getRight().expand(connectionHitboxExpansion, 0.0, connectionHitboxExpansion);
                            }
                            case NORTH, SOUTH -> {
                                box = pair.getRight().expand(connectionHitboxExpansion, connectionHitboxExpansion, 0.0);
                            }
                            case EAST, WEST -> {
                                box = pair.getRight().expand(0.0, connectionHitboxExpansion, connectionHitboxExpansion);
                            }
                            default -> {
                                throw new RuntimeException("Unknown direction %s".formatted(pair.getLeft()));
                            }
                        }

                        if (box.contains(hitPos)) {
                            return VoxelShapes.cuboid(box);
                        }
                    }
                }
            }
        }

        return getCollisionShape();
    }

    public VoxelShape getCollisionShape() {
        // Fail-safe
        if (conduitEntities.size() == 0 || conduitShapes.size() == 0) {
            return VoxelShapes.fullCube();
        }

        ArrayList<VoxelShape> shapes = new ArrayList<>();
        for (ConduitShape shape : conduitShapes) {
            for (var core : shape.cores()) {
                shapes.add(VoxelShapes.cuboid(core.expand(coreHitboxExpansion)));
            }

            for (var connectionPair : shape.connections()) {
                // Expand and clamp the connection boxes to the cube boundaries
                var box = connectionPair.getRight().expand(connectionHitboxExpansion);
                shapes.add(VoxelShapes.cuboid(
                        Math.max(box.minX, 0.0), Math.max(box.minY, 0.0), Math.max(box.minZ, 0.0),
                        Math.min(box.maxX, 1.0), Math.min(box.maxY, 1.0), Math.min(box.maxZ, 1.0)
                ));
            }
        }

        return shapes.stream().reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR))
                .orElseGet(VoxelShapes::fullCube);
    }
}
