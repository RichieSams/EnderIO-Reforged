package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.screens.BuiltScreenHandler;
import richiesams.enderio.reforged.screens.ModScreenHandlers;
import richiesams.enderio.reforged.screens.ScreenHandlerBuilder;

import java.util.Map;

import static richiesams.enderio.reforged.blockentities.ModBlockEntities.ALLOY_SMELTER;

public class StirlingGeneratorBlockEntity extends AbstractMachineBlockEntity {
    public static int burnTimeDivisor = 4;

    private static final int InputSize = 1;
    private static final int OutputSize = 0;
    private static final int BaseEnergyStorageSize = 100000;
    private static final int BaseMaxEnergyInsertion = 0;
    private static final int MaxEnergyExtraction = 120;

    public StirlingGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ALLOY_SMELTER, pos, state,
                InputSize, OutputSize,
                BaseEnergyStorageSize, BaseMaxEnergyInsertion, MaxEnergyExtraction);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void tick(World world, BlockPos pos, BlockState state, StirlingGeneratorBlockEntity entity) {
        if (world == null || world.isClient) {
            return;
        }


    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        Map<Item, Integer> vanillaBurnTimeMap = AbstractFurnaceBlockEntity.createFuelTimeMap();
        if (vanillaBurnTimeMap.containsKey(stack.getItem())) {
            return vanillaBurnTimeMap.get(stack.getItem()) / burnTimeDivisor;
        }

        return 0;
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);


    }

    @Override
    protected void writeNbt(NbtCompound nbt) {

        super.writeNbt(nbt);
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player) {
        return new ScreenHandlerBuilder(ModScreenHandlers.STIRLING_GENERATOR_SCREEN_HANDLER)
                .player(player.getInventory()).addMainInventory(8, 84).addHotbar(8, 142).finish()
                .blockEntity(capacitorInventory).addCapacitorSlot(0, 12, 60).finish()
                .blockEntity(inputsInventory).addSlot(0, 80, 34).finish()
                .build(this, syncID);
    }
}
