package richiesams.enderio.reforged.rendering;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class ConduitBundleModelProvider implements ModelResourceProvider {
    private static final Identifier CONDUIT_BUNDLE_BLOCK = new Identifier(EnderIOReforgedBaseMod.MOD_ID, "block/conduit_bundle");

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if (resourceId.equals(CONDUIT_BUNDLE_BLOCK)) {
            return new ConduitBundleModel();
        }

        return null;
    }
}
