package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.items.ModItems;
import richiesams.enderio.reforged.screens.SimpleAlloySmelterScreenHandler;

public class SimpleAlloySmelterBlockEntity extends MachineBlockEntity {
    public SimpleAlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIMPLE_ALLOY_SMELTER, pos, state, DefaultedList.ofSize(TotalSlots, ItemStack.EMPTY));
    }

    public static final int TotalSlots = 4;
    public static final int InputSlot0 = 0;
    public static final int InputSlot1 = 1;
    public static final int InputSlot2 = 2;
    public static final int OutputSlot = 3;


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SimpleAlloySmelterScreenHandler(syncId, inv, this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MachineBlockEntity entity) {
        if (hasRecipe(entity) && hasNotReachedStackLimit(entity)) {
            craftItem(entity);
        }
    }

    private static void craftItem(MachineBlockEntity entity) {
        entity.removeStack(InputSlot0, 1);
        entity.removeStack(InputSlot1, 1);
        entity.removeStack(InputSlot2, 1);

        entity.setStack(OutputSlot, new ItemStack(ModItems.DARK_STEEL_INGOT,
                entity.getStack(OutputSlot).getCount() + 1));
    }

    private static boolean hasRecipe(MachineBlockEntity entity) {
        boolean hasItemInFirstSlot = entity.getStack(InputSlot0).getItem() == Items.IRON_INGOT;
        boolean hasItemInSecondSlot = entity.getStack(InputSlot1).getItem() == Items.CHARCOAL;
        boolean hasItemInThirdSlot = entity.getStack(InputSlot2).getItem() == Items.IRON_INGOT;

        return hasItemInFirstSlot && hasItemInSecondSlot && hasItemInThirdSlot;
    }

    private static boolean hasNotReachedStackLimit(MachineBlockEntity entity) {
        return entity.getStack(OutputSlot).getCount() < entity.getStack(OutputSlot).getMaxCount();
    }
}
