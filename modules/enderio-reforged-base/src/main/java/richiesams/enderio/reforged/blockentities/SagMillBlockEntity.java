package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.recipes.CrushingRecipe;
import richiesams.enderio.reforged.recipes.RecipeHelper;
import richiesams.enderio.reforged.screens.BuiltScreenHandler;
import richiesams.enderio.reforged.screens.ConsumableItemStatusProvider;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.screens.ScreenHandlerBuilder;
import richiesams.enderio.reforged.util.EnderIOInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static richiesams.enderio.reforged.blockentities.ModBlockEntities.SAG_MILL;

public class SagMillBlockEntity extends AbstractMachineBlockEntity implements ConsumableItemStatusProvider {
    private List<ItemStack> recipeOutputs = new ArrayList<>();
    protected final EnderIOInventory grindingBallInventory;
    protected float grindingBallOutputMultiplier;
    protected float grindingBallChanceMultiplier;
    protected float grindingBallPowerMultiplier;
    protected long grindingBallDurability;
    protected long grindingBallDurabilityMax;
    protected boolean recipeUsesGrindingBall;

    private static final int InputSize = 1;
    private static final int OutputSize = 4;
    private static final int BaseEnergyStorageSize = 100000;
    private static final int BaseMaxEnergyInsertion = 120;
    private static final int MaxEnergyExtraction = 0;

    public SagMillBlockEntity(BlockPos pos, BlockState state) {
        super(SAG_MILL, pos, state,
                InputSize, OutputSize,
                BaseEnergyStorageSize, BaseMaxEnergyInsertion, MaxEnergyExtraction);

        grindingBallInventory = createInventory(this, 1, 1);
        grindingBallOutputMultiplier = 1.0f;
        grindingBallChanceMultiplier = 1.0f;
        grindingBallPowerMultiplier = 1.0f;
        grindingBallDurability = 0;
        grindingBallDurabilityMax = 0;
        recipeUsesGrindingBall = false;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void tick(World world, BlockPos pos, BlockState state, SagMillBlockEntity entity) {
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
            if (entity.energyStorage.amount >= entity.EUPerTick) {
                entity.energyStorage.amount -= entity.EUPerTick;
                ++entity.progress;
                entity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
        } else {
            if (RecipeHelper.addOutputsToInventory(entity.outputsInventory, entity.recipeOutputs)) {
                // Reset
                entity.progress = 0;
                entity.progressTotal = 0;
                entity.EUPerTick = 0;
                entity.recipeOutputs = new ArrayList<>();

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
        if (energyStorage.amount == 0) {
            return;
        }

        RecipeManager manager = world.getRecipeManager();
        Optional<CrushingRecipe> optionalRecipe = manager.getFirstMatch(CrushingRecipe.Type.INSTANCE, inputsInventory, world);
        if (optionalRecipe.isEmpty()) {
            return;
        }

        CrushingRecipe recipe = optionalRecipe.get();
        List<ItemStack> outputItems = new ArrayList<>();
        // We conservatively make sure we have room for ALL outputs. Even chance ones
        for (CrushingRecipe.CrushingOutput output : recipe.getCrushingOutputs()) {
            outputItems.add(output.stack);
        }

        if (RecipeHelper.inventoryCanAcceptRecipeOutputs(outputsInventory, outputItems)) {
            progress = 0;
            progressTotal = recipe.getCookTime();
            EUPerTick = recipe.getEUPerTick();

            // Generate the outputs
            recipeOutputs = new ArrayList<>();
            for (CrushingRecipe.CrushingOutput output : recipe.getCrushingOutputs()) {
                if (output.chance == 1.0f) {
                    recipeOutputs.add(output.stack);
                    continue;
                }

                if (world.random.nextFloat() < output.chance) {
                    recipeOutputs.add(output.stack);
                }
            }

            // Remove the inputs
            RecipeHelper.craftRecipeFromInventory(recipe, inputsInventory);
            updateState(true);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        recipeOutputs = new ArrayList<>();
        NbtList outputs = nbt.getList("RecipeOutputs", 10);
        for (int i = 0; i < outputs.size(); ++i) {
            recipeOutputs.add(ItemStack.fromNbt(outputs.getCompound(i)));
        }
        grindingBallInventory.readNbtList(nbt.getList("GrindingBall", 10));
        grindingBallOutputMultiplier = nbt.getFloat("GrindingBallOutputMultiplier");
        grindingBallChanceMultiplier = nbt.getFloat("GrindingBallChanceMultiplier");
        grindingBallPowerMultiplier = nbt.getFloat("GrindingBallPowerMultiplier");
        grindingBallDurability = nbt.getLong("GrindingBallDurability");
        grindingBallDurabilityMax = nbt.getLong("GrindingBallDurabilityMax");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList outputs = new NbtList();
        for (ItemStack output : recipeOutputs) {
            NbtCompound compound = new NbtCompound();
            output.writeNbt(compound);
            outputs.add(compound);
        }
        nbt.put("GrindingBall", grindingBallInventory.toNbtList());
        nbt.put("RecipeOutputs", outputs);
        nbt.putFloat("GrindingBallOutputMultiplier", grindingBallOutputMultiplier);
        nbt.putFloat("GrindingBallChanceMultiplier", grindingBallChanceMultiplier);
        nbt.putFloat("GrindingBallPowerMultiplier", grindingBallPowerMultiplier);
        nbt.putLong("GrindingBallDurability", grindingBallDurability);
        nbt.putLong("GrindingBallDurabilityMax", grindingBallDurabilityMax);

        super.writeNbt(nbt);
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player) {
        return new ScreenHandlerBuilder(ModScreenHandlers.SAG_MILL_SCREEN_HANDLER)
                .player(player.getInventory()).addMainInventory(8, 84).addHotbar(8, 142).finish()
                .blockEntity(capacitorInventory).addCapacitorSlot(0, 12, 60).finish()
                .blockEntity(inputsInventory).addSlot(0, 80, 12).finish()
                .blockEntity(outputsInventory).addOutputSlot(0, 49, 59).addOutputSlot(1, 70, 59).addOutputSlot(2, 91, 59).addOutputSlot(3, 112, 59).finish()
                .blockEntity(grindingBallInventory).addGrindingBallSlot(0, 122, 23).finish()
                .build(this, syncID);
    }

    @Override
    public float getConsumableScaledDurabilityRemaining() {
        if (grindingBallDurabilityMax == 0) {
            return 0.0f;
        }

        return (float) ((double) grindingBallDurability / (double) grindingBallDurabilityMax);
    }
}
