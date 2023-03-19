package richiesams.enderio.reforged.conduits;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitConnection;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;

public class BasicFluidConduitEntity extends ConduitEntity {
    public BasicFluidConduitEntity(Conduit conduit, ConduitBundleBlockEntity blockEntity) {
        super(conduit, blockEntity);
    }

    @Override
    public boolean tick(World world, BlockPos pos, BlockState state) {
        boolean markDirty = false;

        if (updateConnections) {
            for (Direction direction : Direction.values()) {
                // TODO: We'll need to use fabric API calls to actually check if the other entity contains a conduit
                //       that we can connect to. Or a block that we can connect to
                if (world.getBlockEntity(pos.offset(direction)) instanceof ConduitBundleBlockEntity otherBlockEntity) {
                    connections.put(direction, new ConduitConnection(false, false, false));
                    markDirty = true;
                } else {
                    if (connections.remove(direction) != null) {
                        markDirty = true;
                    }
                }
            }
            updateConnections = false;
        }

        return markDirty;
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
