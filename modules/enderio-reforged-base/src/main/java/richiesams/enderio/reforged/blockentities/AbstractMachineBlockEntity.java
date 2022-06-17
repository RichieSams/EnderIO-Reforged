package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.util.EnderIOInventory;

public abstract class AbstractMachineBlockEntity extends AbstractSimpleMachineBlockEntity {
    protected final EnderIOInventory capacitorInventory;


    public AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inputsSize, int outputsSize) {
        super(type, pos, state, inputsSize, outputsSize);

        capacitorInventory = createInventory(this, 1, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        capacitorInventory.readNbtList(nbt.getList("Capacitor", 10));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("Capacitor", capacitorInventory.toNbtList());
        super.writeNbt(nbt);
    }

    @Override
    public void onBlockDestroyed(World world, BlockPos pos) {
        super.onBlockDestroyed(world, pos);
        ItemScatterer.spawn(world, pos, capacitorInventory);
    }
}
