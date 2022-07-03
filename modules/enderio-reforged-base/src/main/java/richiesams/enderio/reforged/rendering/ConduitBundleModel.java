package richiesams.enderio.reforged.rendering;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.SpriteReference;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConduitBundleModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private final Identifier particleBreakID = new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit/conduit_crossover");

    private final HashMap<Identifier, Sprite> sprites = new HashMap<>();

    // UnbakedModel methods
    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        ArrayList<SpriteIdentifier> dependencies = new ArrayList<>();
        for (var entry : EnderIOReforgedRegistries.CONDUIT.getEntrySet()) {
            dependencies.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().CoreSprite.identifier()));
            dependencies.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().ConnectorOuterSprite.identifier()));
        }

        // Add particle break texture
        dependencies.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, particleBreakID));

        return dependencies;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        // Fetch all the sprites
        for (var entry : EnderIOReforgedRegistries.CONDUIT.getEntrySet()) {
            sprites.put(entry.getValue().CoreSprite.identifier(), textureGetter.apply(
                    new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().CoreSprite.identifier())));
            sprites.put(entry.getValue().ConnectorOuterSprite.identifier(), textureGetter.apply(
                    new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().ConnectorOuterSprite.identifier())));
            if (entry.getValue().ConnectorInnerSprite != null) {
                sprites.put(entry.getValue().ConnectorInnerSprite.identifier(), textureGetter.apply(
                        new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().ConnectorInnerSprite.identifier())));
            }
        }

        // Particle break texture
        sprites.put(particleBreakID, textureGetter.apply(
                new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, particleBreakID)
        ));

        return this;
    }


    // BakedModel methods
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        // We don't need this method, because we use FabricBakedModel instead.
        // However, it's better to not return null in case some mod decides to call this function
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return sprites.get(particleBreakID);
    }

    @Override
    public ModelTransformation getTransformation() {
        return null;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return null;
    }

    // FabricBakedModel methods
    @Override
    public boolean isVanillaAdapter() {
        return false; // False to trigger FabricBakedModel rendering
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        if (blockView instanceof RenderAttachedBlockView renderAttachedBlockView) {
            Object renderAttachment = renderAttachedBlockView.getBlockEntityRenderAttachment(pos);
            if (renderAttachment instanceof ConduitBundleRenderState renderState) {
                // Build the mesh using the Renderer API
                Renderer renderer = RendererAccess.INSTANCE.getRenderer();
                MeshBuilder builder = renderer.meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                for (ConduitBundleRenderState.ConduitRenderState conduitRenderState : renderState.conduitRenderStates()) {
                    Conduit conduit = conduitRenderState.conduit();
                    ConduitShape shape = conduitRenderState.shape();

                    // Render the cores
                    SpriteReference coreSpriteRef = conduit.CoreSprite;
                    for (Box box : shape.cores()) {
                        renderCuboid(emitter, box, coreSpriteRef);
                    }

                    // Now render the connectors
                    // Render the inner texture first, if it exists
                    ArrayList<SpriteReference> connectorSpriteRefs = new ArrayList<>();
                    if (conduit.ConnectorInnerSprite != null) {
                        connectorSpriteRefs.add(conduit.ConnectorInnerSprite);
                    }
                    connectorSpriteRefs.add(conduit.ConnectorOuterSprite);
                    for (SpriteReference spriteRef : connectorSpriteRefs) {
                        Sprite sprite = sprites.get(spriteRef.identifier());

                        for (net.minecraft.util.Pair<Direction, Box> connectionPair : shape.connections()) {
                            Direction direction = connectionPair.getLeft();
                            Box box = connectionPair.getRight();

                            switch (direction) {
                                case UP -> {
                                    renderCuboidFace(emitter, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                                    renderCuboidFace(emitter, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                                    renderCuboidFace(emitter, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                                    renderCuboidFace(emitter, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                                }
                                case DOWN -> {
                                    renderCuboidFace(emitter, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                                    renderCuboidFace(emitter, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                                    renderCuboidFace(emitter, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                                    renderCuboidFace(emitter, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                                }
                                case NORTH -> {
                                    renderCuboidFace(emitter, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                                    renderCuboidFace(emitter, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                                    renderCuboidFace(emitter, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                                    renderCuboidFace(emitter, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                                }
                                case SOUTH -> {
                                    renderCuboidFace(emitter, Direction.EAST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                                    renderCuboidFace(emitter, Direction.WEST, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                                    renderCuboidFace(emitter, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_270);
                                    renderCuboidFace(emitter, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_90);
                                }
                                case EAST -> {
                                    renderCuboidFace(emitter, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                                    renderCuboidFace(emitter, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                                    renderCuboidFace(emitter, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                                    renderCuboidFace(emitter, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                                }
                                case WEST -> {
                                    renderCuboidFace(emitter, Direction.NORTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                                    renderCuboidFace(emitter, Direction.SOUTH, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                                    renderCuboidFace(emitter, Direction.UP, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
                                    renderCuboidFace(emitter, Direction.DOWN, box, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_180);
                                }
                            }
                        }
                    }
                }

                context.meshConsumer().accept(builder.build());
            }
        }
    }

    private void renderCuboid(QuadEmitter emitter, Box cube, SpriteReference spriteRef) {
        Sprite sprite = sprites.get(spriteRef.identifier());

        // Render all the faces
        for (Direction direction : Direction.values()) {
            renderCuboidFace(emitter, direction, cube, sprite, spriteRef.uvFrom(), spriteRef.uvTo(), Rotation.DEGREES_0);
        }
    }

    private void renderCuboidFace(QuadEmitter emitter, Direction direction, Box cube, Sprite sprite, Vec2f uvFrom, Vec2f uvTo, Rotation uvRotation) {
        switch (direction) {
            case DOWN -> {
                renderQuad(emitter, Direction.DOWN,
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
                renderQuad(emitter, Direction.UP,
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
                renderQuad(emitter, Direction.NORTH,
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
                renderQuad(emitter, Direction.SOUTH,
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
                renderQuad(emitter, Direction.WEST,
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
                renderQuad(emitter, Direction.EAST,
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

    private void renderQuad(QuadEmitter emitter, Direction nominalDirection,
                            Vec3f vertex0, Vec3f vertex1, Vec3f vertex2, Vec3f vertex3,
                            Sprite sprite, Vec2f uvFrom, Vec2f uvTo, Rotation uvRotation) {
        emitter.cullFace(nominalDirection);
        emitter.nominalFace(nominalDirection);

        emitter.pos(0, vertex0);
        emitter.pos(1, vertex1);
        emitter.pos(2, vertex2);
        emitter.pos(3, vertex3);

        // Add the texture
        emitter.sprite(0, 0, uvFrom.x, uvFrom.y);
        emitter.sprite(1, 0, uvFrom.x, uvTo.y);
        emitter.sprite(2, 0, uvTo.x, uvTo.y);
        emitter.sprite(3, 0, uvTo.x, uvFrom.y);

        // Rotate the texture
        rotateSprite(emitter, uvRotation);

        // Shift the UV range to fit within the range of the sprite within the atlas
        interpolate(emitter, sprite);

        // Enable texture usage
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();
    }

    private void interpolate(MutableQuadView q, Sprite sprite) {
        final float uMin = sprite.getMinU();
        final float uSpan = sprite.getMaxU() - uMin;
        final float vMin = sprite.getMinV();
        final float vSpan = sprite.getMaxV() - vMin;

        for (int i = 0; i < 4; i++) {
            q.sprite(i, 0, uMin + q.spriteU(i, 0) * uSpan, vMin + q.spriteV(i, 0) * vSpan);
        }
    }

    private void rotateSprite(MutableQuadView q, Rotation rotation) {
        Vec2f uv0 = new Vec2f(q.spriteU(0, 0), q.spriteV(0, 0));
        Vec2f uv1 = new Vec2f(q.spriteU(1, 0), q.spriteV(1, 0));
        Vec2f uv2 = new Vec2f(q.spriteU(2, 0), q.spriteV(2, 0));
        Vec2f uv3 = new Vec2f(q.spriteU(3, 0), q.spriteV(3, 0));

        switch (rotation) {
            case DEGREES_0 -> {
                // Nothing
            }
            case DEGREES_90 -> {
                q.sprite(0, 0, uv1.x, uv1.y);
                q.sprite(1, 0, uv2.x, uv2.y);
                q.sprite(2, 0, uv3.x, uv3.y);
                q.sprite(3, 0, uv0.x, uv0.y);
            }
            case DEGREES_180 -> {
                q.sprite(0, 0, uv2.x, uv2.y);
                q.sprite(1, 0, uv3.x, uv3.y);
                q.sprite(2, 0, uv0.x, uv0.y);
                q.sprite(3, 0, uv1.x, uv1.y);
            }
            case DEGREES_270 -> {
                q.sprite(0, 0, uv3.x, uv3.y);
                q.sprite(1, 0, uv0.x, uv0.y);
                q.sprite(2, 0, uv1.x, uv1.y);
                q.sprite(3, 0, uv2.x, uv2.y);
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        throw new RuntimeException("ConduitBundle should not exist as an item");
    }

    public enum Rotation {
        DEGREES_0,
        DEGREES_90,
        DEGREES_180,
        DEGREES_270
    }
}
