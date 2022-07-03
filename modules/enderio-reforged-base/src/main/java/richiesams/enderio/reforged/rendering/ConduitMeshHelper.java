package richiesams.enderio.reforged.rendering;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import richiesams.enderio.reforged.api.conduits.ConduitOffset;

public class ConduitMeshHelper {
    public static Box CoreFromOffset(ConduitOffset offset) {
        Vec3f from = new Vec3f(6.5f, 6.5f, 6.5f);
        Vec3f to = new Vec3f(9.5f, 9.5f, 9.5f);

        switch (offset) {
            case NONE -> {
                // Nothing to do
            }
            case UP -> {
                from.add(0.0f, 3.0f, 0.0f);
                to.add(0.0f, 3.0f, 0.0f);
            }
            case DOWN -> {
                from.add(0.0f, -3.0f, 0.0f);
                to.add(0.0f, -3.0f, 0.0f);
            }
            case NORTH -> {
                from.add(0.0f, 0.0f, 3.0f);
                to.add(0.0f, 0.0f, 3.0f);
            }
            case SOUTH -> {
                from.add(0.0f, 0.0f, -3.0f);
                to.add(0.0f, 0.0f, -3.0f);
            }
            case EAST -> {
                from.add(3.0f, 0.0f, 0.0f);
                to.add(3.0f, 0.0f, 0.0f);
            }
            case WEST -> {
                from.add(-3.0f, 0.0f, 0.0f);
                to.add(-3.0f, 0.0f, 0.0f);
            }
            case UP_NORTH -> {
                from.add(0.0f, 3.0f, 3.0f);
                to.add(0.0f, 3.0f, 3.0f);
            }
            case UP_SOUTH -> {
                from.add(0.0f, 3.0f, -3.0f);
                to.add(0.0f, 3.0f, -3.0f);
            }
            case UP_EAST -> {
                from.add(3.0f, 3.0f, 0.0f);
                to.add(3.0f, 3.0f, 0.0f);
            }
            case UP_WEST -> {
                from.add(-3.0f, 3.0f, 0.0f);
                to.add(-3.0f, 3.0f, 0.0f);
            }
            case DOWN_NORTH -> {
                from.add(0.0f, -3.0f, 3.0f);
                to.add(0.0f, -3.0f, 3.0f);
            }
            case DOWN_SOUTH -> {
                from.add(0.0f, -3.0f, -3.0f);
                to.add(0.0f, -3.0f, -3.0f);
            }
            case DOWN_EAST -> {
                from.add(3.0f, -3.0f, 0.0f);
                to.add(3.0f, -3.0f, 0.0f);
            }
            case DOWN_WEST -> {
                from.add(-3.0f, -3.0f, 0.0f);
                to.add(-3.0f, -3.0f, 0.0f);
            }
            case NORTH_EAST -> {
                from.add(3.0f, 0.0f, 3.0f);
                to.add(3.0f, 0.0f, 3.0f);
            }
            case NORTH_WEST -> {
                from.add(-3.0f, 0.0f, 3.0f);
                to.add(-3.0f, 0.0f, 3.0f);
            }
            case SOUTH_EAST -> {
                from.add(3.0f, 0.0f, -3.0f);
                to.add(3.0f, 0.0f, -3.0f);
            }
            case SOUTH_WEST -> {
                from.add(-3.0f, 0.0f, -3.0f);
                to.add(-3.0f, 0.0f, -3.0f);
            }
        }

        // Normalize
        from.scale(1.0f / 16.0f);
        to.scale(1.0f / 16.0f);

        return new Box(
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ()
        );
    }

    public static Box ConnectorFromOffset(ConduitOffset offset, Direction connectionDirection) {
        Box connectorCuboid;
        switch (connectionDirection) {
            case DOWN -> {
                connectorCuboid = new Box(
                        7.0, 0.0, 7.0,
                        9.0, 6.5, 9.0
                );
            }
            case UP -> {
                connectorCuboid = new Box(
                        7.0, 9.5, 7.0,
                        9.0, 16.0, 9.0
                );
            }
            case NORTH -> {
                connectorCuboid = new Box(
                        7.0, 7.0, 0.0,
                        9.0, 9.0, 6.5
                );
            }
            case SOUTH -> {
                connectorCuboid = new Box(
                        7.0, 7.0, 9.5,
                        9.0, 9.0, 16.0
                );
            }
            case WEST -> {
                connectorCuboid = new Box(
                        0.0, 7.0, 7.0,
                        6.5, 9.0, 9.0
                );
            }
            case EAST -> {
                connectorCuboid = new Box(
                        9.5, 7.0, 7.0,
                        16.0, 9.0, 9.0
                );
            }
            default -> {
                throw new RuntimeException("Unknown connection direction %s".formatted(connectionDirection.toString()));
            }
        }

        switch (offset) {
            case NONE -> {
                // Nothing to do
            }
            case UP -> {
                connectorCuboid = connectorCuboid.offset(0.0, 3.0, 0.0);
            }
            case DOWN -> {
                connectorCuboid = connectorCuboid.offset(0.0, -3.0, 0.0);
            }
            case NORTH -> {
                connectorCuboid = connectorCuboid.offset(0.0, 0.0, 3.0);
            }
            case SOUTH -> {
                connectorCuboid = connectorCuboid.offset(0.0, 0.0, -3.0);
            }
            case EAST -> {
                connectorCuboid = connectorCuboid.offset(3.0, 0.0, 0.0);
            }
            case WEST -> {
                connectorCuboid = connectorCuboid.offset(-3.0, 0.0, 0.0);
            }
            case UP_NORTH -> {
                connectorCuboid = connectorCuboid.offset(0.0, 3.0, 3.0);
            }
            case UP_SOUTH -> {
                connectorCuboid = connectorCuboid.offset(0.0, 3.0, -3.0);
            }
            case UP_EAST -> {
                connectorCuboid = connectorCuboid.offset(3.0, 3.0, 0.0);
            }
            case UP_WEST -> {
                connectorCuboid = connectorCuboid.offset(-3.0, 3.0, 0.0);
            }
            case DOWN_NORTH -> {
                connectorCuboid = connectorCuboid.offset(0.0, -3.0, 3.0);
            }
            case DOWN_SOUTH -> {
                connectorCuboid = connectorCuboid.offset(0.0, -3.0, -3.0);
            }
            case DOWN_EAST -> {
                connectorCuboid = connectorCuboid.offset(3.0, -3.0, 0.0);
            }
            case DOWN_WEST -> {
                connectorCuboid = connectorCuboid.offset(-3.0, -3.0, 0.0);
            }
            case NORTH_EAST -> {
                connectorCuboid = connectorCuboid.offset(3.0, 0.0, 3.0);
            }
            case NORTH_WEST -> {
                connectorCuboid = connectorCuboid.offset(-3.0, 0.0, 3.0);
            }
            case SOUTH_EAST -> {
                connectorCuboid = connectorCuboid.offset(3.0, 0.0, -3.0);
            }
            case SOUTH_WEST -> {
                connectorCuboid = connectorCuboid.offset(-3.0, 0.0, -3.0);
            }
        }

        // Normalize and return
        return new Box(
                connectorCuboid.minX / 16.0, connectorCuboid.minY / 16.0, connectorCuboid.minZ / 16.0,
                connectorCuboid.maxX / 16.0, connectorCuboid.maxY / 16.0, connectorCuboid.maxZ / 16.0
        );
    }
}
