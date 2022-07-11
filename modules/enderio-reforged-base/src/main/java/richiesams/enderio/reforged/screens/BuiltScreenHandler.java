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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.Range;
import richiesams.enderio.reforged.blockentities.AbstractSimpleMachineBlockEntity;
import richiesams.enderio.reforged.util.Humanize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class BuiltScreenHandler extends ScreenHandler {
    private static final int SlotStride = 18;

    private final Predicate<PlayerEntity> canInteract;

    private final AbstractSimpleMachineBlockEntity blockEntity;

    private final List<Range<Integer>> playerSlotRanges;
    private final List<Range<Integer>> blockEntitySlotRanges;

    protected BuiltScreenHandler(ScreenHandlerType<BuiltScreenHandler> type, int syncId, final Predicate<PlayerEntity> canInteract,
                                 final List<Range<Integer>> playerSlotRanges,
                                 final List<Range<Integer>> blockEntitySlotRanges,
                                 AbstractSimpleMachineBlockEntity blockEntity) {
        super(type, syncId);

        this.canInteract = canInteract;
        this.playerSlotRanges = playerSlotRanges;
        this.blockEntitySlotRanges = blockEntitySlotRanges;

        this.blockEntity = blockEntity;
    }


    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int index) {
        ItemStack originalStack = ItemStack.EMPTY;

        final Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            final ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();

            boolean shifted = false;

            for (final Range<Integer> range : this.playerSlotRanges) {
                if (range.contains(index)) {

                    if (this.shiftToBlockEntity(stackInSlot)) {
                        shifted = true;
                    }
                    break;
                }
            }

            if (!shifted) {
                for (final Range<Integer> range : this.blockEntitySlotRanges) {
                    if (range.contains(index)) {
                        if (this.shiftToPlayer(stackInSlot)) {
                            shifted = true;
                        }
                        break;
                    }
                }
            }

            slot.onQuickTransfer(stackInSlot, originalStack);
            if (stackInSlot.getCount() <= 0) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (stackInSlot.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, stackInSlot);
        }
        return originalStack;

    }

    protected boolean shiftItemStack(final ItemStack stackToShift, final int start, final int end) {
        if (stackToShift.isEmpty()) {
            return false;
        }
        int inCount = stackToShift.getCount();

        // First lets see if we have the same item in a slot to merge with
        for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
            final Slot slot = this.slots.get(slotIndex);
            final ItemStack stackInSlot = slot.getStack();
            int maxCount = Math.min(stackToShift.getMaxCount(), slot.getMaxItemCount());

            if (!stackToShift.isEmpty() && slot.canInsert(stackToShift)) {
                if (ItemStack.areEqual(stackInSlot, stackToShift)) {
                    // Got 2 stacks that need merging
                    int freeStackSpace = maxCount - stackInSlot.getCount();
                    if (freeStackSpace > 0) {
                        int transferAmount = Math.min(freeStackSpace, stackToShift.getCount());
                        stackInSlot.increment(transferAmount);
                        stackToShift.decrement(transferAmount);
                    }
                }
            }
        }

        // If not lets go find the next free slot to insert our remaining stack
        for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
            final Slot slot = this.slots.get(slotIndex);
            final ItemStack stackInSlot = slot.getStack();

            if (stackInSlot.isEmpty() && slot.canInsert(stackToShift)) {
                int maxCount = Math.min(stackToShift.getMaxCount(), slot.getMaxItemCount());

                int moveCount = Math.min(maxCount, stackToShift.getCount());
                ItemStack moveStack = stackToShift.copy();
                moveStack.setCount(moveCount);
                slot.setStack(moveStack);
                stackToShift.decrement(moveCount);
            }
        }

        // If we moved some, but still have more left over lets try again
        if (!stackToShift.isEmpty() && stackToShift.getCount() != inCount) {
            shiftItemStack(stackToShift, start, end);
        }

        return stackToShift.getCount() != inCount;
    }

    private boolean shiftToBlockEntity(final ItemStack stackToShift) {
        for (final Range<Integer> range : this.blockEntitySlotRanges) {
            if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean shiftToPlayer(final ItemStack stackToShift) {
        for (final Range<Integer> range : this.playerSlotRanges) {
            if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1)) {
                return true;
            }
        }
        return false;
    }

    public Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canInteract.test(player);
    }

    public boolean isCrafting() {
        Objects.requireNonNull(blockEntity);
        return blockEntity.getProgressTotal() > 0;
    }

    public float getScaledProgress() {
        Objects.requireNonNull(blockEntity);

        int progress = blockEntity.getProgress();
        int progressTotal = blockEntity.getProgressTotal();

        return (float) progress / (float) progressTotal;
    }

    public float getScaledEnergy() {
        Objects.requireNonNull(blockEntity);

        long current = blockEntity.getCurrentEnergy();
        long capacity = blockEntity.getEnergyCapacity();

        return (float) ((double) current / (double) capacity);
    }

    public List<Text> getTooltipLines() {
        ArrayList<Text> lines = new ArrayList<>();
        if (!Screen.hasShiftDown()) {
            lines.add(new LiteralText("%s / %s EU".formatted(Humanize.number(blockEntity.getCurrentEnergy()), Humanize.number(blockEntity.getEnergyCapacity()))));
            lines.add(new TranslatableText("tooltip.enderio-reforged.more_info"));
        } else {
            lines.add(new LiteralText("%d / %d EU".formatted(blockEntity.getCurrentEnergy(), blockEntity.getEnergyCapacity())));
            lines.add(new TranslatableText("tooltip.enderio-reforged.max_input").append("%d EU/tick".formatted(blockEntity.EnergyStorage.maxInsert)));
            long maxOutput = blockEntity.EnergyStorage.maxExtract;
            if (maxOutput > 0) {
                lines.add(new TranslatableText("tooltip.enderio-reforged.max_output").append("%d EU/tick".formatted(maxOutput)));
            }
        }

        return lines;
    }
}
