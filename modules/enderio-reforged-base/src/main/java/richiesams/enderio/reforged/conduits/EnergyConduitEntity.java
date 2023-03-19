package richiesams.enderio.reforged.conduits;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitConnection;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;
import team.reborn.energy.api.EnergyStorage;

public class EnergyConduitEntity extends ConduitEntity {
    public static String ConduitGroup = "energy";

    private final BlockApiCache<EnergyStorage, Direction>[] adjacentCaches = new BlockApiCache[6];
    public EnergyConduitNetwork network;

    public EnergyConduitEntity(Conduit conduit, ConduitBundleBlockEntity blockEntity) {
        super(conduit, blockEntity);

        for (Direction direction : Direction.values()) {
            adjacentCaches[direction.getId()] = BlockApiCache.create(EnergyStorage.SIDED, (ServerWorld) blockEntity.getWorld(), blockEntity.getPos().offset(direction));
        }
    }

    @Override
    public boolean tick(World world, BlockPos pos, BlockState state) {
        boolean markDirty = false;

        if (updateConnections) {
            for (Direction direction : Direction.values()) {
                BlockApiCache<EnergyStorage, Direction> cache = getAdjacentCache(direction);
                // Ignore blocks that are in unloaded chunks
                // And avoid loading chunks by our search
                if (!world.isChunkLoaded(cache.getPos())) {
                    continue;
                }

                // Check if there is a connection to an EnergyStorage

                EnergyStorage connectionTarget = cache.find(direction.getOpposite());
                if (connectionTarget != null) {
                    // If the target is a Conduit Bundle, check if it contains an energy conduit
                    // If so, connect to it, with no termination or input/output rendering
                    if (cache.getBlockEntity() instanceof ConduitBundleBlockEntity otherBlockEntity) {
                        if (otherBlockEntity.getConduitEntityOfType("energy") != null) {
                            connections.put(direction, new ConduitConnection(false, false, false));
                            continue;
                        }
                    }

                    // Now check if the target supports connections
                    if (connectionTarget.supportsInsertion() || connectionTarget.supportsExtraction()) {
                        connections.put(direction, new ConduitConnection(true, connectionTarget.supportsInsertion(), connectionTarget.supportsExtraction()));
                        continue;
                    }
                } else {

                }

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

    public BlockApiCache<EnergyStorage, Direction> getAdjacentCache(Direction direction) {
        return adjacentCaches[direction.getId()];
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
