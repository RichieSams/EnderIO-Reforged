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
