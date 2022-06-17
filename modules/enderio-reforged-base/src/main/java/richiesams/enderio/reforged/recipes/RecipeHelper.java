package richiesams.enderio.reforged.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class RecipeHelper {
    public static <T extends Inventory> void craftRecipeFromInventory(Recipe<T> recipe, T inventory) {
        DefaultedList<Ingredient> inputs = recipe.getIngredients();

        for (Ingredient input : inputs) {
            boolean found = false;
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack inventoryStack = inventory.getStack(i);

                if (input.test(inventoryStack)) {
                    inventory.removeStack(i, 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "Failed to craft Recipe %s from inventory items - Failed to find %s in the inventory".formatted(
                                recipe.getId().toString(), input.getMatchingStacks()[0].getItem().toString()
                        )
                );
            }
        }
    }

    public static <T extends Inventory> boolean shapelessRecipeMatches(Recipe<T> recipe, T inventory) {
        HashMap<Integer, Integer> hashedInventory = new HashMap<>();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);

            int key = Registry.ITEM.getRawId(itemStack.getItem());
            int count = hashedInventory.getOrDefault(key, 0);
            count += itemStack.getCount();
            hashedInventory.put(key, count);
        }

        HashMap<Integer, Integer> hashedIngredients = new HashMap<>();
        for (Ingredient input : recipe.getIngredients()) {
            for (ItemStack itemStack : input.getMatchingStacks()) {
                int key = Registry.ITEM.getRawId(itemStack.getItem());
                int count = hashedIngredients.getOrDefault(key, 0);
                count += itemStack.getCount();
                hashedIngredients.put(key, count);
            }
        }

        // Now we compare
        for (int itemKey : hashedIngredients.keySet()) {
            // Check if the inventory *has* the required item at all
            if (!hashedInventory.containsKey(itemKey)) {
                return false;
            }
            // Check if the inventory has at least the required amount
            if (hashedInventory.get(itemKey) < hashedIngredients.get(itemKey)) {
                return false;
            }
        }

        return true;
    }

    public static <I extends Inventory> boolean inventoryCanAcceptRecipeOutput(I inventory, ItemStack output) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack inventoryStack = inventory.getStack(i);
            if (inventoryStack.isEmpty()) {
                return true;
            }
            if (inventoryStack.isItemEqualIgnoreDamage(output)) {
                int newCount = inventoryStack.getCount() + output.getCount();
                if (newCount <= inventory.getMaxCountPerStack() && newCount <= inventoryStack.getMaxCount()) {
                    return true;
                }
            }
        }

        return false;
    }
}
