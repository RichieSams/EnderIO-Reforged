package richiesams.enderio.reforged.api.conduits;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;
import richiesams.enderio.reforged.api.util.SerializationUtil;

public class Conduit {
    public final ConduitOffset XOffset;
    public final ConduitOffset YOffset;
    public final ConduitOffset ZOffset;

    public final SpriteReference CoreSprite;
    public final SpriteReference ConnectorOuterSprite;
    public final SpriteReference ConnectorInnerSprite;

    public final String Group;
    public final int Tier;

    private final Conduit.Factory<? extends ConduitEntity> factory;

    public Conduit(JsonObject jsonObject, Conduit.Factory<? extends ConduitEntity> factory) {
        JsonElement group = jsonObject.get("group");
        if (group == null) {
            throw new JsonSyntaxException("Missing \"group\" value in conduit definition");
        }
        this.Group = group.getAsString();

        JsonElement tier = jsonObject.get("tier");
        if (tier == null) {
            this.Tier = 0;
        } else {
            this.Tier = tier.getAsInt();
        }

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
        JsonObject connectorOuter = connector.getAsJsonObject("outer");
        if (connectorOuter == null) {
            throw new JsonSyntaxException("Missing \"connector::outer\" section in conduit definition");
        }
        this.ConnectorOuterSprite = SpriteReference.fromJSON(connectorOuter);
        JsonObject connectorInner = connector.getAsJsonObject("inner");
        if (connectorInner == null) {
            // The inner sprite is optional
            this.ConnectorInnerSprite = null;
        } else {
            this.ConnectorInnerSprite = SpriteReference.fromJSON(connectorInner);
        }


        this.factory = factory;
    }

    public ItemStack toItemStack() {
        Identifier identifier = EnderIOReforgedRegistries.CONDUIT.getId(this);
        Item item = Registry.ITEM.get(identifier);
        if (item == Items.AIR) {
            EnderIOReforgedBaseMod.LOGGER.warn("Failed to get Conduit item for %s".formatted(identifier));
        }

        return item.getDefaultStack();
    }

    public ConduitEntity createConduitEntity() {
        return factory.create(this);
    }

    @FunctionalInterface
    public interface Factory<T extends ConduitEntity> {
        T create(Conduit conduit);
    }
}
