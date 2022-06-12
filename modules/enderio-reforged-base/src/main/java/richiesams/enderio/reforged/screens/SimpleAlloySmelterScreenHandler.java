package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import richiesams.enderio.reforged.blockentities.SimpleAlloySmelterBlockEntity;
import richiesams.enderio.reforged.screens.slots.ModResultSlot;

public class SimpleAlloySmelterScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    private static final int inputSlot0OffsetX = 54;
    private static final int inputslot0OffsetY = 17;
    private static final int inputSlot1OffsetX = 79;
    private static final int inputslot1OffsetY = 7;
    private static final int inputSlot2OffsetX = 103;
    private static final int inputslot2OffsetY = 17;
    private static final int outputOffsetX = 79;
    private static final int outputOffsetY = 58;
    private static final int playerInventoryOffsetX = 8;
    private static final int playerInventoryOffsetY = 84;
    private static final int playerHotbarOffsetX = 8;
    private static final int playerHotbarOffsetY = 142;
    private static final int slotTotalSize = 18;


    public SimpleAlloySmelterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(SimpleAlloySmelterBlockEntity.TotalSlots));
    }

    public SimpleAlloySmelterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.SIMPLE_ALLOY_SMELTER_SCREEN_HANDLER, syncId);
        checkSize(inventory, SimpleAlloySmelterBlockEntity.TotalSlots);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, SimpleAlloySmelterBlockEntity.InputSlot0, inputSlot0OffsetX, inputslot0OffsetY));
        this.addSlot(new Slot(inventory, SimpleAlloySmelterBlockEntity.InputSlot1, inputSlot1OffsetX, inputslot1OffsetY));
        this.addSlot(new Slot(inventory, SimpleAlloySmelterBlockEntity.InputSlot2, inputSlot2OffsetX, inputslot2OffsetY));
        this.addSlot(new ModResultSlot(inventory, SimpleAlloySmelterBlockEntity.OutputSlot, outputOffsetX, outputOffsetY));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(
                        playerInventory,
                        x + y * 9 + 9,
                        playerInventoryOffsetX + x * slotTotalSize,
                        playerInventoryOffsetY + y * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, playerHotbarOffsetX + i * slotTotalSize, playerHotbarOffsetY));
        }
    }
}
