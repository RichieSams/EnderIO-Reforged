package richiesams.enderio.reforged.api.grinding_balls;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.util.StringIdentifiable;

import java.lang.reflect.Type;

@JsonAdapter(GrindingBallBonus.Serializer.class)
public enum GrindingBallBonus implements StringIdentifiable {
    NONE("none"),
    MULTIPLY_OUTPUT("multiply_output"),
    CHANCE_ONLY("chance_only");

    private final String name;

    GrindingBallBonus(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static GrindingBallBonus getBonusByString(String str) {
        for (GrindingBallBonus offset : values()) {
            if (offset.name.equals(str)) {
                return offset;
            }
        }

        return MULTIPLY_OUTPUT;
    }

    static class Serializer implements JsonSerializer<GrindingBallBonus>, JsonDeserializer<GrindingBallBonus> {
        @Override
        public JsonElement serialize(GrindingBallBonus src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.asString());
        }

        @Override
        public GrindingBallBonus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return getBonusByString(json.getAsString());
        }
    }
}
