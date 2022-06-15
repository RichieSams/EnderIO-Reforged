package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.recipes.AlloyingRecipe;
import richiesams.enderio.reforged.recipes.RecipeHelper;
import richiesams.enderio.reforged.recipes.VanillaSmeltingRecipe;
import richiesams.enderio.reforged.screens.AlloySmelterScreenHandler;

import java.util.Optional;

public class AlloySmelterBlockEntity extends MachineBlockEntity {
    public static final int TotalSlots = 5;
    public static final int Input0Slot = 0;
    public static final int Input1Slot = 1;
    public static final int Input2Slot = 2;
    public static final int OutputSlot = 3;
    public static final int CapacitorSlot = 4;

    private Recipe<Inventory> currentRecipe = null;

    public AlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLOY_SMELTER, pos, state, DefaultedList.ofSize(TotalSlots, ItemStack.EMPTY));
    }

    public static void tick(World world, BlockPos pos, BlockState state, AlloySmelterBlockEntity entity) {
        if (world == null || world.isClient) {
            return;
        }

        if (entity.currentRecipe == null) {
            entity.updateRecipe(world);
        }

        if (entity.currentRecipe == null) {
            return;
        }

        if (entity.progress < entity.progressTotal) {
            ++entity.progress;
        } else {
            if (RecipeHelper.inventoryCanAcceptRecipeOutput(entity.currentRecipe, entity, OutputSlot)) {
                // Set the output
                ItemStack craftingOutput = entity.currentRecipe.craft(new SimpleInventory());
                ItemStack currentOutput = entity.getStack(OutputSlot);
                if (currentOutput.isEmpty()) {
                    entity.setStack(OutputSlot, craftingOutput);
                } else {
                    currentOutput.increment(craftingOutput.getCount());
                    entity.setStack(OutputSlot, currentOutput);
                }

                // Reset
                entity.progress = 0;
                entity.progressTotal = 0;
                entity.currentRecipe = null;
            }
        }
    }

    private void updateRecipe(World world) {
        SimpleInventory testInventory = new SimpleInventory(3);
        testInventory.setStack(0, getStack(Input0Slot));
        testInventory.setStack(1, getStack(Input1Slot));
        testInventory.setStack(2, getStack(Input2Slot));

        RecipeManager manager = world.getRecipeManager();
        Optional<AlloyingRecipe> alloyingRecipe = manager.getFirstMatch(AlloyingRecipe.Type.INSTANCE, testInventory, world);
        if (alloyingRecipe.isPresent() && RecipeHelper.inventoryCanAcceptRecipeOutput(alloyingRecipe.get(), this, OutputSlot)) {
            currentRecipe = alloyingRecipe.get();
            progress = 0;
            progressTotal = alloyingRecipe.get().getCookTime();

            // Remove the inputs
            RecipeHelper.craftRecipeFromInventory(alloyingRecipe.get(), testInventory);
            setStack(Input0Slot, testInventory.getStack(0));
            setStack(Input1Slot, testInventory.getStack(1));
            setStack(Input2Slot, testInventory.getStack(2));
            updateState(true);
            return;
        }

        ItemStack currentOutput = getStack(OutputSlot);
        int maxStacks = getMaxCountPerStack();
        if (!currentOutput.isEmpty()) {
            maxStacks = Math.min(maxStacks, currentOutput.getMaxCount());
        }

        Optional<VanillaSmeltingRecipe> smeltingRecipe = VanillaSmeltingRecipe.getFirstMatch(world, testInventory, currentOutput, maxStacks);
        if (smeltingRecipe.isPresent() && RecipeHelper.inventoryCanAcceptRecipeOutput(smeltingRecipe.get(), this, OutputSlot)) {
            currentRecipe = smeltingRecipe.get();
            progress = 0;
            progressTotal = smeltingRecipe.get().getCookTime();

            // Remove the inputs
            RecipeHelper.craftRecipeFromInventory(smeltingRecipe.get(), testInventory);
            setStack(Input0Slot, testInventory.getStack(0));
            setStack(Input1Slot, testInventory.getStack(1));
            setStack(Input2Slot, testInventory.getStack(2));
            updateState(true);
            return;
        }

        updateState(false);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.enderio-reforged.alloy_smelter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlloySmelterScreenHandler(syncId, inv, this, this.propertyDelegate);
    }
}
