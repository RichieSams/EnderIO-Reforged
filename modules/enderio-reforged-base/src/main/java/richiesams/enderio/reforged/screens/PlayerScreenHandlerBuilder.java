package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.Range;

public class PlayerScreenHandlerBuilder {
    private final ScreenHandlerBuilder parent;
    private final PlayerInventory playerInventory;
    private Range<Integer> mainInventory;
    private Range<Integer> hotbar;


    PlayerScreenHandlerBuilder(final ScreenHandlerBuilder parent, final PlayerInventory playerInventory) {
        this.parent = parent;
        this.playerInventory = playerInventory;
    }

    public PlayerScreenHandlerBuilder addMainInventory(final int xOffset, final int yOffset) {
        final int startIndex = this.parent.slots.size();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.parent.slots.add(new Slot(playerInventory, x + y * 9 + 9, xOffset + x * 18, yOffset + y * 18));
            }
        }

        mainInventory = Range.between(startIndex, parent.slots.size() - 1);
        return this;
    }

    public PlayerScreenHandlerBuilder addHotbar(final int xOffset, final int yOffset) {
        final int startIndex = this.parent.slots.size();
        for (int i = 0; i < 9; ++i) {
            parent.slots.add(new Slot(playerInventory, i, xOffset + i * 18, yOffset));
        }

        hotbar = Range.between(startIndex, parent.slots.size() - 1);
        return this;
    }

    public ScreenHandlerBuilder finish() {
        if (mainInventory != null) {
            parent.addPlayerInventoryRange(mainInventory);
        }
        if (hotbar != null) {
            parent.addPlayerInventoryRange(hotbar);
        }

        return parent;
    }
}
