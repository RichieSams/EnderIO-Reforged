package richiesams.enderio.reforged.blockentities;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class EnderIOInventory extends SimpleInventory {
    public EnderIOInventory(int size) {
        super(size);
    }

    @Override
    public void readNbtList(NbtList nbtList) {
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            stacks.set(i, ItemStack.fromNbt(nbtCompound));
        }
    }

    @Override
    public NbtList toNbtList() {
        NbtList nbtList = new NbtList();
        for (ItemStack itemStack : stacks) {
            NbtCompound nbtCompound = new NbtCompound();
            itemStack.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }

        return nbtList;
    }
}
