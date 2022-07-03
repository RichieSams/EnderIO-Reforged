package richiesams.enderio.reforged.api.conduits;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;

import java.util.HashMap;

public abstract class ConduitEntity {
    protected final Conduit conduit;
    protected HashMap<Direction, ConduitConnection> connections;
    protected boolean updateConnections;

    protected ConduitEntity(Conduit conduit) {
        this.conduit = conduit;
        this.connections = new HashMap<>();
        this.updateConnections = true;
    }

    public abstract boolean tick(World world, BlockPos pos, BlockState state);

    public void readNbt(NbtCompound nbt) {
        HashMap<Direction, ConduitConnection> newConnections = new HashMap<>();
        NbtList connectionsList = nbt.getList("Connections", 10);
        for (int i = 0; i < connectionsList.size(); ++i) {
            NbtCompound connection = connectionsList.getCompound(i);
            Direction direction = Direction.byName(connection.getString("Direction"));
            boolean terminated = connection.getBoolean("Terminated");
            boolean input = connection.getBoolean("Input");
            boolean output = connection.getBoolean("Output");

            newConnections.put(direction, new ConduitConnection(terminated, input, output));
        }

        this.connections = newConnections;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("id", EnderIOReforgedRegistries.CONDUIT.getId(conduit).toString());
        NbtList connectionsList = new NbtList();
        for (var entry : connections.entrySet()) {
            NbtCompound connection = new NbtCompound();
            connection.putString("Direction", entry.getKey().toString());
            connection.putBoolean("Terminated", entry.getValue().terminated());
            connection.putBoolean("Input", entry.getValue().input());
            connection.putBoolean("Output", entry.getValue().output());

            connectionsList.add(connection);
        }
        nbt.put("Connections", connectionsList);
    }

    public void markConnectionsDirty() {
        updateConnections = true;
    }

    public Conduit getBackingConduit() {
        return conduit;
    }

    public HashMap<Direction, ConduitConnection> getConnections() {
        return connections;
    }

    @Nullable
    public static ConduitEntity fromNBT(NbtCompound nbt) {
        String identifierStr = nbt.getString("id");
        Identifier identifier = Identifier.tryParse(identifierStr);
        if (identifier == null) {
            EnderIOReforgedBaseMod.LOGGER.error("Conduit entity has invalid type: %s".formatted(identifierStr));
            return null;
        }
        return EnderIOReforgedRegistries.CONDUIT.getOrEmpty(identifier).map(type -> {
            try {
                return type.createConduitEntity();
            } catch (Throwable throwable) {
                EnderIOReforgedBaseMod.LOGGER.error("Failed to create conduit entity %s - %s".formatted(identifierStr, throwable));
                return null;
            }
        }).map(conduitEntity -> {
            try {
                conduitEntity.readNbt(nbt);
                return conduitEntity;
            } catch (Throwable throwable) {
                EnderIOReforgedBaseMod.LOGGER.error("Failed to load data for conduit entity %s - %s".formatted(identifierStr, throwable));
                return null;
            }
        }).orElseGet(() -> {
            EnderIOReforgedBaseMod.LOGGER.warn("Skipping ConduitEntity with id %s".formatted(identifierStr));
            return null;
        });
    }
}
