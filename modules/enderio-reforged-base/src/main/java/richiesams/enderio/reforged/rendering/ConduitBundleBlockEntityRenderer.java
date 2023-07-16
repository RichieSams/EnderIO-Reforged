package richiesams.enderio.reforged.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class ConduitBundleBlockEntityRenderer implements BlockEntityRenderer<ConduitBundleBlockEntity> {
    private final HashMap<Identifier, Sprite> sprites = new HashMap<>();

    public final Identifier particleBreakID = new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit/conduit_crossover");

    public ConduitBundleBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        var textureGetter = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        // Fetch all the sprites
        for (var entry : EnderIOReforgedRegistries.CONDUIT.getEntrySet()) {
            Identifier coreID = entry.getValue().CoreSprite.identifier();
            Sprite coreSprite = textureGetter.apply(coreID);
            sprites.put(coreID, coreSprite);

            Identifier connectorOuterID = entry.getValue().ConnectorOuterSprite.identifier();
            Sprite connectorOuterSprite = textureGetter.apply(connectorOuterID);
            sprites.put(connectorOuterID, connectorOuterSprite);


            if (entry.getValue().ConnectorInnerSprite != null) {
                Identifier connectorInnerID = entry.getValue().ConnectorInnerSprite.identifier();
                Sprite connectorInnerSprite = textureGetter.apply(connectorInnerID);
                sprites.put(connectorInnerID, connectorInnerSprite);
            }
        }

        // Particle break texture
        sprites.put(particleBreakID, textureGetter.apply(particleBreakID));
    }

    @Override
    public void render(ConduitBundleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        entity.render(sprites, matrices, vertexConsumers, light, overlay);
    }
}
