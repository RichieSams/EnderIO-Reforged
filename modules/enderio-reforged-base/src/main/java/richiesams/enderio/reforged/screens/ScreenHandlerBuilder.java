package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Range;
import richiesams.enderio.reforged.blockentities.AbstractSimpleMachineBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ScreenHandlerBuilder {
    private final ScreenHandlerType<BuiltScreenHandler> type;
    final List<Slot> slots;
    private final List<Range<Integer>> playerInventoryRanges;
    private final List<Range<Integer>> blockEntityInventoryRanges;


    public ScreenHandlerBuilder(ScreenHandlerType<BuiltScreenHandler> type) {
        this.type = type;
        this.slots = new ArrayList<>();
        this.playerInventoryRanges = new ArrayList<>();
        this.blockEntityInventoryRanges = new ArrayList<>();
    }

    private static Predicate<PlayerEntity> isUsable(AbstractSimpleMachineBlockEntity blockEntity) {
        return playerEntity -> blockEntity.getWorld().getBlockEntity(blockEntity.getPos()) == blockEntity
                && playerEntity.getPos().distanceTo(Vec3d.of(blockEntity.getPos())) < 16;
    }

    public PlayerScreenHandlerBuilder player(PlayerInventory inventory) {
        return new PlayerScreenHandlerBuilder(this, inventory);
    }

    public BlockEntityScreenHandlerBuilder blockEntity(Inventory blockEntityInventory) {
        return new BlockEntityScreenHandlerBuilder(this, blockEntityInventory);
    }

    public void addPlayerInventoryRange(final Range<Integer> range) {
        playerInventoryRanges.add(range);
    }

    public void addBlockEntityRange(final Range<Integer> range) {
        blockEntityInventoryRanges.add(range);
    }

    public BuiltScreenHandler build(final AbstractSimpleMachineBlockEntity blockEntity, int syncID) {
        final BuiltScreenHandler built = new BuiltScreenHandler(type, syncID, isUsable(blockEntity),
                playerInventoryRanges, blockEntityInventoryRanges,
                blockEntity);

        slots.forEach(built::addSlot);
        slots.clear();

        return built;
    }
}
