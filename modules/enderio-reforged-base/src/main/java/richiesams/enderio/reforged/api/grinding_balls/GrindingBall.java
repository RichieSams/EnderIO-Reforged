package richiesams.enderio.reforged.api.grinding_balls;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class GrindingBall {
    public final float outputMultiplier;
    public final float chanceMultiplier;
    public final float powerMultiplier;
    public final long durability;

    GrindingBall(JsonObject jsonObject) {
        JsonElement outputMultiplier = jsonObject.get("output_multiplier");
        if (outputMultiplier == null) {
            throw new JsonSyntaxException("Missing \"output_multiplier\" value in grinding ball definition");
        }
        this.outputMultiplier = outputMultiplier.getAsFloat();

        JsonElement chanceMultiplier = jsonObject.get("chance_multiplier");
        if (chanceMultiplier == null) {
            throw new JsonSyntaxException("Missing \"output_multiplier\" value in grinding ball definition");
        }
        this.chanceMultiplier = chanceMultiplier.getAsFloat();

        JsonElement powerMultiplier = jsonObject.get("power_multiplier");
        if (powerMultiplier == null) {
            throw new JsonSyntaxException("Missing \"power_multiplier\" value in grinding ball definition");
        }
        this.powerMultiplier = powerMultiplier.getAsFloat();

        JsonElement durability = jsonObject.get("durability");
        if (durability == null) {
            throw new JsonSyntaxException("Missing \"durability\" value in grinding ball definition");
        }
        this.durability = durability.getAsLong();
    }
}
