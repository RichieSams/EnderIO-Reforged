package richiesams.enderio.reforged.conduits;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;

public class ItemConduitEntity extends ConduitEntity {
    public ItemConduitEntity(Conduit conduit) {
        super(conduit);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }
}
