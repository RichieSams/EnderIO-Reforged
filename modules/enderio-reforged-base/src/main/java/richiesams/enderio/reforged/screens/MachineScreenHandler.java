package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class MachineScreenHandler extends ScreenHandler {
    private static final int slotStride = 18;

    private final Inventory inventory;
    protected final PropertyDelegate delegate;

    public MachineScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
                                Inventory inventory, int internalSlotCount, PropertyDelegate delegate) {
        super(type, syncId);
        checkSize(inventory, internalSlotCount);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.delegate = delegate;

        addProperties(delegate);
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

    protected void addPlayerInventory(PlayerInventory playerInventory, int playerInventoryOffsetX, int playerInventoryOffsetY) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(
                        playerInventory,
                        x + y * 9 + 9,
                        playerInventoryOffsetX + x * slotStride,
                        playerInventoryOffsetY + y * 18));
            }
        }
    }

    protected void addPlayerHotbar(PlayerInventory playerInventory, int playerHotbarOffsetX, int playerHotbarOffsetY) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, playerHotbarOffsetX + i * slotStride, playerHotbarOffsetY));
        }
    }
}
