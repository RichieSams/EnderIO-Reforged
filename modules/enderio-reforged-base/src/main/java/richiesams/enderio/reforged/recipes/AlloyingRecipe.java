package richiesams.enderio.reforged.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class AlloyingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> inputs;
    private final int power;
    private final int time;

    public AlloyingRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> inputs, int power, int time) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.power = power;
        this.time = time;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return RecipeHelper.shapelessRecipeMatches(this, inventory);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return inputs;
    }

    public int getCookTime() {
        return time;
    }

    public int getEUPerTick() {
        return power;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AlloyingRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "alloying";
    }

    public static class Serializer implements RecipeSerializer<AlloyingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "alloying";

        @Override
        public AlloyingRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> inputs = Serializer.getIngredients(JsonHelper.getArray(json, "ingredients"));
            if (inputs.isEmpty()) {
                throw new JsonParseException("No ingredients for alloying recipe");
            }
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            int power = JsonHelper.getInt(json, "power");
            int time = JsonHelper.getInt(json, "time");

            return new AlloyingRecipe(id, output, inputs, power, time);
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(json.size(), Ingredient.EMPTY);
            for (int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i));
                if (ingredient.isEmpty()) {
                    continue;
                }
                defaultedList.set(i, ingredient);
            }
            return defaultedList;
        }

        @Override
        public AlloyingRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            int power = buf.readInt();
            int time = buf.readInt();

            return new AlloyingRecipe(id, output, inputs, power, time);
        }

        @Override
        public void write(PacketByteBuf buf, AlloyingRecipe recipe) {
            DefaultedList<Ingredient> inputs = recipe.getIngredients();
            buf.writeInt(inputs.size());
            for (Ingredient input : inputs) {
                input.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());

            buf.writeInt(recipe.power);
            buf.writeInt(recipe.time);
        }
    }
}
