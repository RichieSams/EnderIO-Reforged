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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

                float left = 6.5f / 16.0f;
                float right = 9.5f / 16.0f;

                for (Conduit conduit : renderState.conduits()) {
                    SpriteReference coreSpriteRef = conduit.CoreSprite;
                    Sprite coreSprite = sprites.get(coreSpriteRef.identifier());
                    float coreSpriteWidth = coreSprite.getWidth();
                    float coreSpriteHeight = coreSprite.getHeight();

                    for (Direction direction : Direction.values()) {
                        emitter.square(direction, 6.5f / 16.0f, 6.5f / 16.0f, 9.5f / 16.0f, 9.5f / 16.0f, 6.5f / 16.0f);

                        // Add the texture
                        emitter.sprite(0, 0, coreSpriteRef.uvFrom().x / coreSpriteWidth, coreSpriteRef.uvFrom().y / coreSpriteHeight);
                        emitter.sprite(1, 0, coreSpriteRef.uvFrom().x / coreSpriteWidth, coreSpriteRef.uvTo().y / coreSpriteHeight);
                        emitter.sprite(2, 0, coreSpriteRef.uvTo().x / coreSpriteWidth, coreSpriteRef.uvTo().y / coreSpriteHeight);
                        emitter.sprite(3, 0, coreSpriteRef.uvTo().x / coreSpriteWidth, coreSpriteRef.uvFrom().y / coreSpriteHeight);

                        // Shift the UV range to fit within the range of the sprite within the atlas
                        interpolate(emitter, 0, coreSprite);

                        // Enable texture usage
                        emitter.spriteColor(0, -1, -1, -1, -1);
                        emitter.emit();
                    }
                }

                context.meshConsumer().accept(builder.build());
            }
        }
    }

    private static void interpolate(MutableQuadView q, int spriteIndex, Sprite sprite) {
        final float uMin = sprite.getMinU();
        final float uSpan = sprite.getMaxU() - uMin;
        final float vMin = sprite.getMinV();
        final float vSpan = sprite.getMaxV() - vMin;

        for (int i = 0; i < 4; i++) {
            q.sprite(i, spriteIndex, uMin + q.spriteU(i, spriteIndex) * uSpan, vMin + q.spriteV(i, spriteIndex) * vSpan);
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        throw new RuntimeException("ConduitBundle should not exist as an item");
    }
}
