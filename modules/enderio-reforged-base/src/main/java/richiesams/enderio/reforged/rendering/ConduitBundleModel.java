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
import richiesams.enderio.reforged.api.conduits.ConduitOffset;
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
            dependencies.add(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().ConnectorSprite.identifier()));
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
            sprites.put(entry.getValue().ConnectorSprite.identifier(), textureGetter.apply(
                    new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue().ConnectorSprite.identifier())));
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

                if (renderState.conduits().size() == 1) {
                    SpriteReference coreSpriteRef = renderState.conduits().get(0).CoreSprite;
                    Box cube = ConduitMeshHelper.CoreFromOffset(ConduitOffset.NONE);

                    renderCuboid(emitter, cube, coreSpriteRef);
                } else {
                    for (Conduit conduit : renderState.conduits()) {
                        SpriteReference coreSpriteRef = conduit.CoreSprite;
                        Box cube = ConduitMeshHelper.CoreFromOffset(conduit.XOffset);

                        renderCuboid(emitter, cube, coreSpriteRef);
                    }
                }

                context.meshConsumer().accept(builder.build());
            }
        }
    }

    private void renderCuboid(QuadEmitter emitter, Box cube, SpriteReference spriteReference) {
        Sprite sprite = sprites.get(spriteReference.identifier());

        float spriteWidth = sprite.getWidth();
        float spriteHeight = sprite.getHeight();

        Vec2f normalizedSpriteUVFrom = new Vec2f(spriteReference.uvFrom().x / spriteWidth, spriteReference.uvFrom().y / spriteHeight);
        Vec2f normalizedSpriteUVTo = new Vec2f(spriteReference.uvTo().x / spriteWidth, spriteReference.uvTo().y / spriteHeight);

        // Render all the faces
        for (Direction direction : Direction.values()) {
            renderCuboidFace(emitter, direction, cube, sprite, normalizedSpriteUVFrom, normalizedSpriteUVTo);
        }
    }

    private void renderCuboidFace(QuadEmitter emitter, Direction direction, Box cube, Sprite sprite, Vec2f normalizedSpriteUVFrom, Vec2f normalizedSpriteUVTo) {
        switch (direction) {
            case DOWN -> {
                renderQuad(emitter, Direction.DOWN,
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.minZ),
                        sprite,
                        normalizedSpriteUVFrom,
                        normalizedSpriteUVTo
                );
            }
            case UP -> {
                renderQuad(emitter, Direction.UP,
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.maxZ),
                        sprite,
                        normalizedSpriteUVFrom,
                        normalizedSpriteUVTo
                );
            }
            case NORTH -> {
                renderQuad(emitter, Direction.NORTH,
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.minZ),
                        sprite,
                        normalizedSpriteUVFrom,
                        normalizedSpriteUVTo
                );
            }
            case SOUTH -> {
                renderQuad(emitter, Direction.SOUTH,
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ),
                        sprite,
                        normalizedSpriteUVFrom,
                        normalizedSpriteUVTo
                );
            }
            case WEST -> {
                renderQuad(emitter, Direction.WEST,
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.minX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.minX, (float) cube.maxY, (float) cube.maxZ),
                        sprite,
                        normalizedSpriteUVFrom,
                        normalizedSpriteUVTo
                );
            }
            case EAST -> {
                renderQuad(emitter, Direction.EAST,
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.maxZ),
                        new Vec3f((float) cube.maxX, (float) cube.minY, (float) cube.minZ),
                        new Vec3f((float) cube.maxX, (float) cube.maxY, (float) cube.minZ),
                        sprite,
                        normalizedSpriteUVFrom,
                        normalizedSpriteUVTo
                );
            }
        }
    }

    private void renderQuad(QuadEmitter emitter, Direction nominalDirection, Vec3f vertex0, Vec3f vertex1, Vec3f vertex2, Vec3f vertex3, Sprite sprite, Vec2f normalizedSpriteUVFrom, Vec2f normalizedSpriteUVTo) {
        emitter.cullFace(nominalDirection);
        emitter.nominalFace(nominalDirection);

        emitter.pos(0, vertex0);
        emitter.pos(1, vertex1);
        emitter.pos(2, vertex2);
        emitter.pos(3, vertex3);

        // Add the texture
        emitter.sprite(0, 0, normalizedSpriteUVFrom.x, normalizedSpriteUVFrom.y);
        emitter.sprite(1, 0, normalizedSpriteUVFrom.x, normalizedSpriteUVTo.y);
        emitter.sprite(2, 0, normalizedSpriteUVTo.x, normalizedSpriteUVTo.y);
        emitter.sprite(3, 0, normalizedSpriteUVTo.x, normalizedSpriteUVFrom.y);

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

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        throw new RuntimeException("ConduitBundle should not exist as an item");
    }
}
