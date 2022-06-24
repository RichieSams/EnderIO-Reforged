package richiesams.enderio.reforged.api.conduits;

import com.google.gson.JsonObject;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;
import richiesams.enderio.reforged.api.util.SerializationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConduitRegistryUtil {
    public static Conduit registerConduit(
            Identifier identifier,
            ConduitRegistryUtil.Factory<? extends Conduit> conduitFactory,
            Conduit.Factory<? extends ConduitEntity> conduitEntityFactory) {
        String location = "assets/%s/conduits/%s.json".formatted(identifier.getNamespace(), identifier.getPath());

        JsonObject jsonObject;
        try {
            InputStream inputStream = FabricLauncherBase.getLauncher().getResourceAsStream(location);
            if (inputStream == null) {
                throw new RuntimeException("Failed to find resource file %s".formatted(location));
            }

            jsonObject = SerializationUtil.GSON.fromJson(IOUtils.toString(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read conduit resource file %s".formatted(location), e);
        }

        return Registry.register(EnderIOReforgedRegistries.CONDUIT, identifier, conduitFactory.create(jsonObject, conduitEntityFactory));
    }

    @FunctionalInterface
    public interface Factory<T extends Conduit> {
        T create(JsonObject jsonObject, Conduit.Factory<? extends ConduitEntity> conduitEntityFactory);
    }
}
