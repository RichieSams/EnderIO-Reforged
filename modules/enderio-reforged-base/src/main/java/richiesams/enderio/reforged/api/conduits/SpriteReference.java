package richiesams.enderio.reforged.api.conduits;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import richiesams.enderio.reforged.api.util.SerializationUtil;

import java.lang.reflect.Type;
import java.util.List;

public record SpriteReference(
        Identifier identifier,
        Vec2f uvFrom,
        Vec2f uvTo
) {
    private static final Type uvListType;

    static {
        //noinspection UnstableApiUsage
        uvListType = new TypeToken<List<Float>>() {
        }.getType();
    }


    static SpriteReference fromJSON(JsonObject jsonObject) {
        JsonElement textureElement = jsonObject.get("texture");
        if (textureElement == null) {
            throw new JsonSyntaxException("Missing \"texture\" element in SpriteReference");
        }

        String textureStr = textureElement.getAsString();
        Identifier textureIdentifier = Identifier.tryParse(textureStr);
        if (textureIdentifier == null) {
            throw new JsonSyntaxException("Invalid texture identifier %s".formatted(textureStr));
        }
        List<Float> UVs = SerializationUtil.GSON.fromJson(jsonObject.get("uv"), uvListType);
        if (UVs.size() != 4) {
            throw new JsonSyntaxException("Invalid uv section - Requires 4 values, given %d".formatted(UVs.size()));
        }

        return new SpriteReference(
                textureIdentifier,
                // Normalize the coordinates
                new Vec2f(UVs.get(0) / 16.0f, UVs.get(1) / 16.0f), new Vec2f(UVs.get(2) / 16.0f, UVs.get(3) / 16.0f)
        );
    }
}
