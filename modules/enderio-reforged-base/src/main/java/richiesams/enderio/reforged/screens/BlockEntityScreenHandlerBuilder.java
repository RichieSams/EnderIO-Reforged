package richiesams.enderio.reforged.screens;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.Range;
import richiesams.enderio.reforged.screens.slots.OutputSlot;

public class BlockEntityScreenHandlerBuilder {
    private final ScreenHandlerBuilder parent;
    private final Inventory inventory;
    private final int rangeStart;

    public BlockEntityScreenHandlerBuilder(final ScreenHandlerBuilder parent, final Inventory blockEntityInventory) {
        this.parent = parent;
        this.inventory = blockEntityInventory;
        this.rangeStart = parent.slots.size();
    }

    public BlockEntityScreenHandlerBuilder addSlot(final int index, final int xOffset, final int yOffset) {
        parent.slots.add(new Slot(this.inventory, index, xOffset, yOffset));
        return this;
    }

    public BlockEntityScreenHandlerBuilder addOutputSlot(final int index, final int xOffset, final int yOffset) {
        parent.slots.add(new OutputSlot(this.inventory, index, xOffset, yOffset));
        return this;
    }

    public BlockEntityScreenHandlerBuilder addCapacitorSlot(final int index, final int xOffset, final int yOffset) {
        parent.slots.add(new Slot(this.inventory, index, xOffset, yOffset));
        return this;
    }

    public ScreenHandlerBuilder finish() {
        parent.addBlockEntityRange(Range.between(this.rangeStart, parent.slots.size() - 1));
        return parent;
    }
}
