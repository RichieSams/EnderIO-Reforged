package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.Range;
import richiesams.enderio.reforged.blockentities.AbstractSimpleMachineBlockEntity;

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


}
