package richiesams.enderio.reforged.api.conduits;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import richiesams.enderio.reforged.api.util.SerializationUtil;

public class Conduit {
    public final ConduitOffset XOffset;
    public final ConduitOffset YOffset;
    public final ConduitOffset ZOffset;

    public final SpriteReference CoreSprite;
    public final SpriteReference ConnectorSprite;

    private final Conduit.Factory<? extends ConduitEntity> factory;

    public Conduit(JsonObject jsonObject, Conduit.Factory<? extends ConduitEntity> factory) {
        this.XOffset = SerializationUtil.GSON.fromJson(jsonObject.get("xOffset"), ConduitOffset.class);
        this.YOffset = SerializationUtil.GSON.fromJson(jsonObject.get("yOffset"), ConduitOffset.class);
        this.ZOffset = SerializationUtil.GSON.fromJson(jsonObject.get("zOffset"), ConduitOffset.class);

        JsonObject core = jsonObject.getAsJsonObject("core");
        if (core == null) {
            throw new JsonSyntaxException("Missing \"core\" section in conduit definition");
        }
        this.CoreSprite = SpriteReference.fromJSON(core);

        JsonObject connector = jsonObject.getAsJsonObject("connector");
        if (connector == null) {
            throw new JsonSyntaxException("Missing \"connector\" section in conduit definition");
        }
        this.ConnectorSprite = SpriteReference.fromJSON(connector);

        this.factory = factory;
    }

    public ConduitEntity createConduitEntity() {
        return factory.create(this);
    }

    @FunctionalInterface
    public interface Factory<T extends ConduitEntity> {
        T create(Conduit conduit);
    }
}
