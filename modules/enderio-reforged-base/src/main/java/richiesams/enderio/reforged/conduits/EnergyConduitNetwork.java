package richiesams.enderio.reforged.conduits;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class EnergyConduitNetwork {
    static long globalTickCounter = 0;

    public EnergyStorage innerStorage;

    private List<EnergyConduitEntity> conduits;
    private List<EnergyStorage> targets;
    private long tickCounter;

    public boolean initialize(EnergyConduitEntity startingEntity) {
        ServerWorld world = (ServerWorld) startingEntity.getHostingBlockEntity().getWorld();

        Conduit backingConduit = startingEntity.getBackingConduit();

        conduits = new ArrayList<>();
        targets = new ArrayList<>();
        tickCounter = 0;

        EnergyConduitNetwork thisNetwork = this;
        boolean newNetwork = true;

        // Do a breadth-first search
        Queue<EnergyConduitEntity> queue = new ArrayDeque<>();

        EnergyConduitEntity current = startingEntity;
        while (current != null) {
            BlockPos currentPos = current.getHostingBlockEntity().getPos();

            for (Direction direction : Direction.values()) {
                // Ignore blocks that are in unloaded chunks
                // And avoid loading chunks by our search
                if (!world.isChunkLoaded(currentPos.offset(direction))) {
                    continue;
                }

                BlockApiCache<EnergyStorage, Direction> cache = current.getAdjacentCache(direction);

                if (cache.getBlockEntity() instanceof ConduitBundleBlockEntity conduitBundle) {
                    EnergyConduitEntity energyEntity = conduitBundle.getConduitOfType(EnergyConduitEntity.class);
                    if (energyEntity == null) {
                        // Bundle doesn't have an energy conduit
                        continue;
                    }

                    if (energyEntity.network == null) {
                        thisNetwork.conduits.add(energyEntity);
                        queue.add(energyEntity);
                        energyEntity.network = thisNetwork;
                        continue;
                    }
                    if (energyEntity.network == thisNetwork) {
                        // We got a loop
                        // Just ignore this conduit
                        continue;
                    }

                    // We found two networks that can be joined
                    // This will happen, for example, if a chunk is re-loaded that has conduits in it
                    //
                    // Check if the other network has ticked yet
                    if (energyEntity.network.tickCounter == globalTickCounter) {
                        // The other network has already ticked
                        // Add ourselves to the existing network and continue the breadth-first-search
                        // It means these new conduits / targets won't be ticked this round,
                        // but they will next tick, which is fine
                        energyEntity.network.conduits.addAll(thisNetwork.conduits);
                        energyEntity.network.targets.addAll(thisNetwork.targets);

                        // Now clear and update the thisNetwork reference
                        thisNetwork.conduits.clear();
                        thisNetwork.targets.clear();
                        thisNetwork = energyEntity.network;

                        newNetwork = false;
                    } else {
                        // The other network hasn't ticked yet
                        // Migrate all the conduits / targets from the other network into this one
                        for (EnergyConduitEntity otherEntity : energyEntity.network.conduits) {
                            otherEntity.network = thisNetwork;
                            thisNetwork.conduits.add(otherEntity);
                        }
                        thisNetwork.targets.addAll(energyEntity.network.targets);

                        // And clear the network, just-in-case
                        // It *should* become dereferenced by anything and GC'd
                        // But we want to make sure entities aren't double ticked
                        energyEntity.network.conduits.clear();
                        energyEntity.network.targets.clear();
                    }
                } else {
                    EnergyStorage target = cache.find(direction.getOpposite());
                    if (target != null) {
                        targets.add(target);
                    }
                }
            }

            current = queue.poll();
        }

        return newNetwork;
    }

    public void tick() {

    }

    static {
        ServerTickEvents.START_SERVER_TICK.register(server -> globalTickCounter++);
    }
}
