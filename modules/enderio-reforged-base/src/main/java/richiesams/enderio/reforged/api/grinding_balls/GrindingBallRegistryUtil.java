package richiesams.enderio.reforged.api.grinding_balls;

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

public class GrindingBallRegistryUtil {
    public static GrindingBall registerGrindingBall(
            Identifier identifier) {
        String location = "assets/%s/grinding_balls/%s.json".formatted(identifier.getNamespace(), identifier.getPath());

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

        return Registry.register(EnderIOReforgedRegistries.GRINDING_BALL, identifier, new GrindingBall(jsonObject));
    }
}
