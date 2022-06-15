package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import richiesams.enderio.reforged.blocks.MachineBlock;

public abstract class MachineBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    protected final DefaultedList<ItemStack> inventory;
    protected final PropertyDelegate propertyDelegate;
    protected int progress = 0;
    protected int progressTotal = 0;

    public static final int ProgressPropertyIndex = 0;
    public static final int ProgressTotalPropertyIndex = 1;
    public static final int PropertyCount = 2;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, DefaultedList<ItemStack> inventory) {
        super(type, pos, state);
        this.inventory = inventory;
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case ProgressPropertyIndex -> MachineBlockEntity.this.progress;
                    case ProgressTotalPropertyIndex -> MachineBlockEntity.this.progressTotal;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case ProgressPropertyIndex -> MachineBlockEntity.this.progress = value;
                    case ProgressTotalPropertyIndex -> MachineBlockEntity.this.progressTotal = value;
                    default -> { /* NOP */ }
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("Progress");
        progressTotal = nbt.getInt("ProgressTotal");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("Progress", progress);
        nbt.putInt("ProgressTotal", progressTotal);
    }

    protected void updateState(boolean lit) {
        Block block = getWorld().getBlockState(pos).getBlock();

        if (block instanceof MachineBlock base) {
            base.setLit(lit, world, pos);
        }
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

}
