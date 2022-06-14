package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.recipes.AlloyingRecipe;
import richiesams.enderio.reforged.recipes.RecipeHelper;
import richiesams.enderio.reforged.screens.SimpleAlloySmelterScreenHandler;

import java.util.Optional;

public class SimpleAlloySmelterBlockEntity extends MachineBlockEntity {
    public static final int TotalSlots = 4;
    private static final int Input0Slot = 0;
    private static final int Input1Slot = 1;
    private static final int Input2Slot = 2;
    private static final int OutputSlot = 3;


    public SimpleAlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIMPLE_ALLOY_SMELTER, pos, state, DefaultedList.ofSize(TotalSlots, ItemStack.EMPTY));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SimpleAlloySmelterScreenHandler(syncId, inv, this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SimpleAlloySmelterBlockEntity entity) {
        // Check if there is a valid recipe for us to craft
        // And if we have space in our output
        SimpleInventory craftingInventory = new SimpleInventory(3);
        craftingInventory.setStack(0, entity.getStack(Input0Slot));
        craftingInventory.setStack(1, entity.getStack(Input1Slot));
        craftingInventory.setStack(2, entity.getStack(Input2Slot));

        RecipeManager manager = world.getRecipeManager();
        Optional<AlloyingRecipe> alloyingMatch = manager.getFirstMatch(AlloyingRecipe.Type.INSTANCE, craftingInventory, world);
        if (alloyingMatch.isPresent() && RecipeHelper.inventoryCanAcceptRecipeOutput(alloyingMatch.get(), entity, OutputSlot)) {
            // Craft the alloy recipe
            AlloyingRecipe recipe = alloyingMatch.get();

            // Set the output
            ItemStack craftingOutput = recipe.craft(craftingInventory);
            ItemStack currentOutput = entity.getStack(OutputSlot);
            if (currentOutput.isEmpty()) {
                entity.setStack(OutputSlot, craftingOutput);
            } else {
                currentOutput.increment(craftingOutput.getCount());
                entity.setStack(OutputSlot, currentOutput);
            }

            // Remove the inputs
            RecipeHelper.craftRecipe(recipe, craftingInventory);
            entity.setStack(Input0Slot, craftingInventory.getStack(0));
            entity.setStack(Input1Slot, craftingInventory.getStack(1));
            entity.setStack(Input2Slot, craftingInventory.getStack(2));
        }
    }
}
