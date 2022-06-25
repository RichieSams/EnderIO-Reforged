package richiesams.enderio.reforged.rendering;

import net.minecraft.util.math.Box;
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
}
