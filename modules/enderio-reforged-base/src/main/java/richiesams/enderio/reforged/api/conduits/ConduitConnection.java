package richiesams.enderio.reforged.api.conduits;

import net.minecraft.util.math.Direction;

public record ConduitConnection(
        Direction direction,
        boolean terminated,
        boolean input,
        boolean output
) {
}
