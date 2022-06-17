// This class is heavily inspired from / copies a significant amount of code from RebornCore
// Credit where credit is due:

/*
 * This file is part of RebornCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 TeamReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package richiesams.enderio.reforged.screens;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.Range;
import richiesams.enderio.reforged.screens.slots.CapacitorSlot;
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
        parent.slots.add(new CapacitorSlot(this.inventory, index, xOffset, yOffset));
        return this;
    }

    public ScreenHandlerBuilder finish() {
        parent.addBlockEntityRange(Range.between(this.rangeStart, parent.slots.size() - 1));
        return parent;
    }
}
