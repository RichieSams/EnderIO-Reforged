package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import richiesams.enderio.reforged.blockentities.AlloySmelterBlockEntity;
import richiesams.enderio.reforged.blockentities.MachineBlockEntity;
import richiesams.enderio.reforged.screens.slots.ModResultSlot;

public class AlloySmelterScreenHandler extends MachineScreenHandler {
    private static final int capacitorOffsetX = 12;
    private static final int capacitorOffsetY = 60;
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


    public AlloySmelterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(AlloySmelterBlockEntity.TotalSlots), new ArrayPropertyDelegate(MachineBlockEntity.PropertyCount));
    }

    public AlloySmelterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(ModScreenHandlers.ALLOY_SMELTER_SCREEN_HANDLER, syncId, playerInventory, inventory, AlloySmelterBlockEntity.TotalSlots, delegate);

        this.addSlot(new Slot(inventory, AlloySmelterBlockEntity.Input0Slot, inputSlot0OffsetX, inputslot0OffsetY));
        this.addSlot(new Slot(inventory, AlloySmelterBlockEntity.Input1Slot, inputSlot1OffsetX, inputslot1OffsetY));
        this.addSlot(new Slot(inventory, AlloySmelterBlockEntity.Input2Slot, inputSlot2OffsetX, inputslot2OffsetY));
        this.addSlot(new ModResultSlot(inventory, AlloySmelterBlockEntity.OutputSlot, outputOffsetX, outputOffsetY));
        // TODO: Make a "CapacitorSlot" class to restrict the types allowed in the capacitor slot
        this.addSlot(new Slot(inventory, AlloySmelterBlockEntity.CapacitorSlot, capacitorOffsetX, capacitorOffsetY));

        addPlayerInventory(playerInventory, playerInventoryOffsetX, playerInventoryOffsetY);
        addPlayerHotbar(playerInventory, playerHotbarOffsetX, playerHotbarOffsetY);
    }

    public boolean isCrafting() {
        return delegate.get(MachineBlockEntity.ProgressPropertyIndex) > 0;
    }

    public float getScaledProgress() {
        int progress = delegate.get(MachineBlockEntity.ProgressPropertyIndex);
        int progressTotal = delegate.get(MachineBlockEntity.ProgressTotalPropertyIndex);

        return (float) progress / (float) progressTotal;
    }
}
