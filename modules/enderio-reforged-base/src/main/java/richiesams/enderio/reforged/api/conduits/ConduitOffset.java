package richiesams.enderio.reforged.api.conduits;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.util.StringIdentifiable;

import java.lang.reflect.Type;

@JsonAdapter(ConduitOffset.Serializer.class)
public enum ConduitOffset implements StringIdentifiable {
    NONE("none"),
    UP("up"),
    DOWN("down"),
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west"),
    UP_NORTH("up_north"),
    UP_SOUTH("up_south"),
    UP_EAST("up_east"),
    UP_WEST("up_west"),
    DOWN_NORTH("down_north"),
    DOWN_SOUTH("down_south"),
    DOWN_EAST("down_east"),
    DOWN_WEST("down_west"),
    NORTH_EAST("north_east"),
    NORTH_WEST("north_west"),
    SOUTH_EAST("south_east"),
    SOUTH_WEST("south_west");

    private final String name;

    ConduitOffset(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    static ConduitOffset getConduitOffsetByString(String str) {
        for (ConduitOffset offset : values()) {
            if (offset.name.equals(str)) {
                return offset;
            }
        }

        return NONE;
    }

    static class Serializer implements JsonSerializer<ConduitOffset>, JsonDeserializer<ConduitOffset> {
        @Override
        public JsonElement serialize(ConduitOffset src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.asString());
        }

        @Override
        public ConduitOffset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return getConduitOffsetByString(json.getAsString());
        }
    }
}
