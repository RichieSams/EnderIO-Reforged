package richiesams.enderio.reforged.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.blockentities.StirlingGeneratorBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class VanillaSmeltingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> inputs;
    private final float experience;
    private final int power;

    private VanillaSmeltingRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> inputs, float experience, int power) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.experience = experience;
        this.power = power;
    }

    public static Optional<VanillaSmeltingRecipe> getFirstMatch(World world, Inventory inventory, ItemStack currentOutput, int maxOutputStacks) {
        RecipeManager manager = world.getRecipeManager();

        ArrayList<SmeltingRecipe> validRecipes = new ArrayList<>();
        for (int i = 0; i < inventory.size(); ++i) {
            SimpleInventory craftingInventory = new SimpleInventory(inventory.getStack(i));

            Optional<SmeltingRecipe> smeltingRecipe = manager.getFirstMatch(RecipeType.SMELTING, craftingInventory, world);
            smeltingRecipe.ifPresent(validRecipes::add);
        }

        if (validRecipes.size() == 0) {
            return Optional.empty();
        }

        // Now attempt to match the output type
        int outputId = Registry.ITEM.getRawId(currentOutput.getItem());
        SmeltingRecipe pickedRecipe = null;
        for (SmeltingRecipe recipe : validRecipes) {
            if (Registry.ITEM.getRawId(recipe.getOutput().getItem()) == outputId) {
                pickedRecipe = recipe;
                break;
            }
        }

        // If we didn't find one, just pick the first one
        if (pickedRecipe == null) {
            pickedRecipe = validRecipes.get(0);
        }

        // Now we see how many we can process at a time
        HashMap<Integer, Integer> hashedInventory = new HashMap<>();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);

            int key = Registry.ITEM.getRawId(itemStack.getItem());
            int count = hashedInventory.getOrDefault(key, 0);
            count += itemStack.getCount();
            hashedInventory.put(key, count);
        }

        DefaultedList<Ingredient> ingredients = pickedRecipe.getIngredients();
        if (ingredients.size() != 1) {
            throw new RuntimeException("Expected only a single ingredient for the vanilla smelting recipe {} but got {}".formatted(pickedRecipe.getId(), ingredients.size()));
        }
        Ingredient ingredient = ingredients.get(0);

        int count = 0;
        for (ItemStack stack : ingredient.getMatchingStacks()) {
            int key = Registry.ITEM.getRawId(stack.getItem());
            if (hashedInventory.containsKey(key)) {
                count = hashedInventory.get(key);
                break;
            }
        }
        if (count == 0) {
            // This *shouldn't* happen, but just in case...
            return Optional.empty();
        }
        count = Math.min(count, 3);
        count = Math.min(count, maxOutputStacks - currentOutput.getCount());

        for (int i = 1; i < count; ++i) {
            ingredients.add(i, ingredient);
        }

        ItemStack output = pickedRecipe.getOutput().copy();
        output.setCount(count);

        int power = pickedRecipe.getCookTime() / StirlingGeneratorBlockEntity.burnTimeDivisor * 10;
        return Optional.of(new VanillaSmeltingRecipe(
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, Serializer.ID),
                output,
                ingredients,
                pickedRecipe.getExperience() * count,
                pickedRecipe.getCookTime() * count * 6)
        );
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        // We never want someone to use this directly
        return false;
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

    public int getRecipePowerCost() {
        return power;
    }

    public float getExperience() {
        return experience;
    }

    public int getEUPerTick() {
        return 6;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return VanillaSmeltingRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return VanillaSmeltingRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<VanillaSmeltingRecipe> {
        private Type() {
        }

        public static final VanillaSmeltingRecipe.Type INSTANCE = new VanillaSmeltingRecipe.Type();
        public static final String ID = "vanilla_smelting";
    }

    public static class Serializer implements RecipeSerializer<VanillaSmeltingRecipe> {
        public static final VanillaSmeltingRecipe.Serializer INSTANCE = new VanillaSmeltingRecipe.Serializer();
        public static final String ID = "vanilla_smelting";

        @Override
        public VanillaSmeltingRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> inputs = VanillaSmeltingRecipe.Serializer.getIngredients(JsonHelper.getArray(json, "ingredients"));
            if (inputs.isEmpty()) {
                throw new JsonParseException("No ingredients for vanilla smelting recipe");
            }
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            float experience = JsonHelper.getFloat(json, "experience");
            int time = JsonHelper.getInt(json, "time");

            return new VanillaSmeltingRecipe(id, output, inputs, experience, time);
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
        public VanillaSmeltingRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            float experience = buf.readFloat();
            int power = buf.readInt();

            return new VanillaSmeltingRecipe(id, output, inputs, experience, power);
        }

        @Override
        public void write(PacketByteBuf buf, VanillaSmeltingRecipe recipe) {
            DefaultedList<Ingredient> inputs = recipe.getIngredients();
            buf.writeInt(inputs.size());
            for (Ingredient input : inputs) {
                input.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());

            buf.writeFloat(recipe.experience);
            buf.writeInt(recipe.power);
        }
    }
}