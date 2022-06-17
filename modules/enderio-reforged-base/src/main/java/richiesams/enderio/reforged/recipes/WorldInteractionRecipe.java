package richiesams.enderio.reforged.recipes;

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

public class WorldInteractionRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Ingredient top;
    private final Ingredient bottom;

    private final Identifier eventType;

    public WorldInteractionRecipe(Identifier id, ItemStack output, Ingredient top, Ingredient bottom, Identifier eventType) {
        this.id = id;
        this.output = output;
        this.top = top;
        this.bottom = bottom;
        this.eventType = eventType;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return top.test(inventory.getStack(0)) && bottom.test(inventory.getStack(1));
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
        return DefaultedList.copyOf(Ingredient.EMPTY, top, bottom);
    }

    public Identifier getEventType() {
        return this.eventType;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WorldInteractionRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return WorldInteractionRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<WorldInteractionRecipe> {
        private Type() {
        }

        public static final WorldInteractionRecipe.Type INSTANCE = new WorldInteractionRecipe.Type();
        public static final String ID = "world_interaction";
    }

    public static class Serializer implements RecipeSerializer<WorldInteractionRecipe> {
        public static final WorldInteractionRecipe.Serializer INSTANCE = new WorldInteractionRecipe.Serializer();
        public static final String ID = "world_interaction";

        @Override
        public WorldInteractionRecipe read(Identifier id, JsonObject json) {
            Ingredient top = Ingredient.fromJson(json.get("top"));
            Ingredient bottom = Ingredient.fromJson(json.get("bottom"));
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            String eventString = JsonHelper.getString(json, "event");
            Identifier eventType = Identifier.tryParse(eventString);
            if (eventType == null) {
                throw new JsonParseException("Invalid event identifier %s".formatted(eventString));
            }

            return new WorldInteractionRecipe(id, output, top, bottom, eventType);
        }

        @Override
        public WorldInteractionRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient top = Ingredient.fromPacket(buf);
            Ingredient bottom = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();

            String eventString = buf.readString();
            Identifier eventType = Identifier.tryParse(eventString);
            if (eventType == null) {
                throw new RuntimeException("Invalid event identifier %s".formatted(eventString));
            }

            return new WorldInteractionRecipe(id, output, top, bottom, eventType);
        }

        @Override
        public void write(PacketByteBuf buf, WorldInteractionRecipe recipe) {
            recipe.top.write(buf);
            recipe.bottom.write(buf);
            buf.writeItemStack(recipe.getOutput());
            buf.writeString(recipe.eventType.toString());
        }
    }
}
