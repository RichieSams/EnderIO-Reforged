package richiesams.enderio.reforged.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;

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
        return inventoryCanAcceptRecipeOutputs(inventory, List.of(output));
    }

    // NOTE: The ItemStacks in outputs will be mutated
    public static <I extends Inventory> boolean inventoryCanAcceptRecipeOutputs(I inventory, List<ItemStack> outputs) {
        // Create a copy of the ItemStacks in the inventory
        // So we can do comparisons without worrying about messing up the real inventory
        SimpleInventory copiedInventory = new SimpleInventory(inventory.size());
        for (int i = 0; i < inventory.size(); ++i) {
            copiedInventory.setStack(i, inventory.getStack(i).copy());
        }

        return addOutputsToInventory(copiedInventory, outputs);
    }

    public static <I extends Inventory> boolean addOutputsToInventory(I inventory, List<ItemStack> outputs) {
        outputLoop:
        for (ItemStack output : outputs) {
            if (output.isEmpty()) {
                continue;
            }

            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack inventoryStack = inventory.getStack(i);
                if (inventoryStack.isEmpty()) {
                    inventory.setStack(i, output);
                    continue outputLoop;
                }
                if (inventoryStack.isItemEqualIgnoreDamage(output)) {
                    int maxCount = Math.min(inventory.getMaxCountPerStack(), inventoryStack.getMaxCount());
                    if (inventoryStack.getCount() >= maxCount) {
                        continue;
                    }

                    int maxTransfer = maxCount - inventoryStack.getCount();
                    int transferCount = Math.min(output.getCount(), maxTransfer);
                    inventoryStack.increment(transferCount);
                    output.decrement(transferCount);

                    if (output.isEmpty()) {
                        continue outputLoop;
                    }
                }
            }

            if (!output.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
