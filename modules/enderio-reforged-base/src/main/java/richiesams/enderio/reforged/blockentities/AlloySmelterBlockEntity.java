package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.recipes.AlloyingRecipe;
import richiesams.enderio.reforged.recipes.RecipeHelper;
import richiesams.enderio.reforged.recipes.VanillaSmeltingRecipe;
import richiesams.enderio.reforged.screens.BuiltScreenHandler;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.screens.ScreenHandlerBuilder;

import java.util.Optional;

public class AlloySmelterBlockEntity extends AbstractMachineBlockEntity {
    private ItemStack recipeOutput = ItemStack.EMPTY;

    private static final int InputSize = 3;
    private static final int OutputSize = 1;
    private static final int BaseEnergyStorageSize = 100000;
    private static final int BaseMaxEnergyInsertion = 120;
    private static final int MaxEnergyExtraction = 0;

    public AlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLOY_SMELTER, pos, state,
                InputSize, OutputSize,
                BaseEnergyStorageSize, BaseMaxEnergyInsertion, MaxEnergyExtraction);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void tick(World world, BlockPos pos, BlockState state, AlloySmelterBlockEntity entity) {
        if (world == null || world.isClient) {
            return;
        }

        // TODO: Reduce computations by only checking for new recipes every 20 ticks (1 second)
        if (entity.progressTotal == 0) {
            entity.updateRecipe(world);
        }

        if (entity.progressTotal == 0) {
            return;
        }


        if (entity.progress < entity.progressTotal) {
            if (entity.EnergyStorage.amount >= entity.EUPerTick) {
                entity.EnergyStorage.amount -= entity.EUPerTick;
                ++entity.progress;
                entity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
        } else {
            if (RecipeHelper.inventoryCanAcceptRecipeOutput(entity.outputsInventory, entity.recipeOutput)) {
                // Set the output
                ItemStack currentOutput = entity.outputsInventory.getStack(0);
                if (currentOutput.isEmpty()) {
                    entity.outputsInventory.setStack(0, entity.recipeOutput);
                } else {
                    currentOutput.increment(entity.recipeOutput.getCount());
                    entity.outputsInventory.setStack(0, currentOutput);
                }

                // Reset
                entity.progress = 0;
                entity.progressTotal = 0;
                entity.EUPerTick = 0;
                entity.recipeOutput = null;

                entity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                entity.updateState(false);
            }
        }
    }

    private void updateRecipe(World world) {
        // Fast out for the "idle" machine case
        if (inputsInventory.isEmpty()) {
            return;
        }

        // Fast out for no energy
        if (EnergyStorage.amount == 0) {
            return;
        }

        RecipeManager manager = world.getRecipeManager();
        Optional<AlloyingRecipe> alloyingRecipe = manager.getFirstMatch(AlloyingRecipe.Type.INSTANCE, inputsInventory, world);
        if (alloyingRecipe.isPresent() && RecipeHelper.inventoryCanAcceptRecipeOutput(outputsInventory, alloyingRecipe.get().craft(inputsInventory))) {
            AlloyingRecipe recipe = alloyingRecipe.get();
            progress = 0;
            progressTotal = recipe.getCookTime();
            EUPerTick = recipe.getEUPerTick();
            recipeOutput = recipe.craft(inputsInventory);

            // Remove the inputs
            RecipeHelper.craftRecipeFromInventory(alloyingRecipe.get(), inputsInventory);
            updateState(true);
            return;
        }

        ItemStack currentOutput = outputsInventory.getStack(0);
        int maxStacks = outputsInventory.getMaxCountPerStack();
        if (!currentOutput.isEmpty()) {
            maxStacks = Math.min(maxStacks, currentOutput.getMaxCount());
        }

        Optional<VanillaSmeltingRecipe> smeltingRecipe = VanillaSmeltingRecipe.getFirstMatch(world, inputsInventory, currentOutput, maxStacks);
        if (smeltingRecipe.isPresent() && RecipeHelper.inventoryCanAcceptRecipeOutput(outputsInventory, smeltingRecipe.get().craft(inputsInventory))) {
            VanillaSmeltingRecipe recipe = smeltingRecipe.get();
            progress = 0;
            progressTotal = recipe.getCookTime();
            EUPerTick = recipe.getEUPerTick();
            recipeOutput = recipe.craft(inputsInventory);

            // Remove the inputs
            RecipeHelper.craftRecipeFromInventory(recipe, inputsInventory);
            updateState(true);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        recipeOutput = null;
        if (nbt.contains("RecipeOutput")) {
            recipeOutput = ItemStack.fromNbt(nbt.getCompound("RecipeOutput"));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (recipeOutput != null) {
            nbt.put("RecipeOutput", recipeOutput.writeNbt(new NbtCompound()));
        }
        super.writeNbt(nbt);
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player) {
        return new ScreenHandlerBuilder(ModScreenHandlers.ALLOY_SMELTER_SCREEN_HANDLER)
                .player(player.getInventory()).addMainInventory(8, 84).addHotbar(8, 142).finish()
                .blockEntity(capacitorInventory).addCapacitorSlot(0, 12, 60).finish()
                .blockEntity(inputsInventory).addSlot(0, 54, 17).addSlot(1, 79, 7).addSlot(2, 103, 17).finish()
                .blockEntity(outputsInventory).addOutputSlot(0, 79, 58).finish()
                .build(this, syncID);
    }
}
