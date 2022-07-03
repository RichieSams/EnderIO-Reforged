package richiesams.enderio.reforged.api.conduits;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.api.EnderIOReforgedRegistries;

import java.util.Arrays;
import java.util.List;

public abstract class ConduitEntity {
    protected final Conduit conduit;
    protected final List<ConduitConnection> connections;

    protected ConduitEntity(Conduit conduit) {
        this.conduit = conduit;
        this.connections = Arrays.stream(Direction.values()).map((Direction direction) -> new ConduitConnection(direction, false, false, false)).toList();
    }

    public abstract void tick(World world, BlockPos pos, BlockState state);

    public void readNbt(NbtCompound nbt) {
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("id", EnderIOReforgedRegistries.CONDUIT.getId(conduit).toString());
    }

    public Conduit getBackingConduit() {
        return conduit;
    }

    public List<ConduitConnection> getConnections() {
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
