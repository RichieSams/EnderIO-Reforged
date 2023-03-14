package richiesams.enderio.reforged.screens.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import richiesams.enderio.reforged.util.ModTags;

public class GrindingBallSlot extends Slot {
    public GrindingBallSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isIn(ModTags.Items.GRINDING_BALLS);
    }
}
