package richiesams.enderio.reforged.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import richiesams.enderio.reforged.api.grinding_balls.GrindingBallBonus;
import richiesams.enderio.reforged.api.util.SerializationUtil;

import java.util.ArrayList;
import java.util.List;

public class CrushingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final List<CrushingOutput> outputs;
    private final Ingredient input;
    private final int power;
    private final int time;
    private final GrindingBallBonus bonus;

    public CrushingRecipe(Identifier id, List<CrushingOutput> outputs, Ingredient input, int power, int time, GrindingBallBonus bonus) {
        this.id = id;
        this.outputs = outputs;
        this.input = input;
        this.power = power;
        this.time = time;
        this.bonus = bonus;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return RecipeHelper.shapelessRecipeMatches(this, inventory);
    }

    @Override
    // This method is useless, since we have multiple outputs
    // So we just return EMPTY
    public ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    // This method is useless, since we have multiple outputs
    // So we just return EMPTY
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.copyOf(Ingredient.EMPTY, input);
    }

    public List<CrushingOutput> getCrushingOutputs() {
        // Deep copy the ItemStacks
        // So we can be sure calls don't mess with the recipe values
        List<CrushingOutput> copiedOutputs = new ArrayList<>(outputs.size());
        for (CrushingOutput output : outputs) {
            copiedOutputs.add(new CrushingOutput(output.stack.copy(), output.chance));
        }

        return copiedOutputs;
    }

    public int getRecipePowerCost() {
        return power;
    }

    public GrindingBallBonus getBonus() {
        return bonus;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class CrushingOutput {
        public final ItemStack stack;
        public final float chance;

        CrushingOutput(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = chance;
        }
    }

    public static class Type implements RecipeType<CrushingRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "crushing";
    }

    public static class Serializer implements RecipeSerializer<CrushingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "crushing";

        @Override
        public CrushingRecipe read(Identifier id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));

            List<CrushingOutput> outputs = Serializer.getOutputs(JsonHelper.getArray(json, "results"));

            int power = JsonHelper.getInt(json, "power");
            int time = JsonHelper.getInt(json, "time");

            // Default to MULTIPLY_OUTPUT, if it doesn't exist
            GrindingBallBonus bonus = GrindingBallBonus.MULTIPLY_OUTPUT;
            if (json.has("bonus")) {
                bonus = SerializationUtil.GSON.fromJson(json.get("bonus"), GrindingBallBonus.class);
            }

            return new CrushingRecipe(id, outputs, input, power, time, bonus);
        }

        private static List<CrushingOutput> getOutputs(JsonArray json) {
            List<CrushingOutput> outputs = new ArrayList<>(json.size());
            for (int i = 0; i < json.size(); ++i) {
                JsonElement outputJson = json.get(i);

                if (outputJson.isJsonNull() || !outputJson.isJsonObject()) {
                    throw new JsonParseException("Invalid output format in index %d".formatted(i));
                }

                JsonObject output = outputJson.getAsJsonObject();
                Item item = ShapedRecipe.getItem(output);
                int count = JsonHelper.getInt(output, "count");
                float chance = JsonHelper.getFloat(output, "chance", 1.0f);

                outputs.add(new CrushingOutput(new ItemStack(item, count), chance));
            }

            return outputs;
        }

        @Override
        public CrushingRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient input = Ingredient.fromPacket(buf);

            int outputsLen = buf.readInt();
            List<CrushingOutput> outputs = new ArrayList<>(outputsLen);
            for (int i = 0; i < outputsLen; ++i) {
                ItemStack stack = buf.readItemStack();
                float chance = buf.readFloat();

                outputs.add(new CrushingOutput(stack, chance));
            }

            int power = buf.readInt();
            int time = buf.readInt();

            GrindingBallBonus bonus = GrindingBallBonus.getBonusByString(buf.readString());

            return new CrushingRecipe(id, outputs, input, power, time, bonus);
        }

        @Override
        public void write(PacketByteBuf buf, CrushingRecipe recipe) {
            recipe.input.write(buf);

            for (CrushingOutput output : recipe.outputs) {
                buf.writeItemStack(output.stack);
                buf.writeFloat(output.chance);
            }

            buf.writeInt(recipe.power);
            buf.writeInt(recipe.time);

            buf.writeString(recipe.bonus.toString());
        }
    }
}
