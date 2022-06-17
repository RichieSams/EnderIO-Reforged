package richiesams.enderio.reforged.events;

import net.minecraft.block.Blocks;
import net.minecraft.events.FireEvents;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import richiesams.enderio.reforged.recipes.WorldInteractionRecipe;

import java.util.List;

public class WorldCraftingEvents {
    public static void registerWorldCraftingEvents() {
        registerFireDiedCrafting();
    }

    private static void registerFireDiedCrafting() {
        FireEvents.FIRE_DIED.event.register(((world, pos) -> {
            ItemStack top = new ItemStack(Blocks.FIRE.asItem());
            ItemStack bottom = new ItemStack(world.getBlockState(pos.down()).getBlock().asItem());

            Identifier event = FireEvents.FIRE_DIED.id;

            SimpleInventory inventory = new SimpleInventory(top, bottom);
            List<WorldInteractionRecipe> recipes = world.getRecipeManager().getAllMatches(WorldInteractionRecipe.Type.INSTANCE, inventory, world);
            for (WorldInteractionRecipe recipe : recipes) {
                if (recipe.getEventType().equals(event)) {
                    ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, recipe.craft(inventory)));
                }
            }
        }));
    }
}
