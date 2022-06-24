package richiesams.enderio.reforged.rendering;

import net.minecraft.util.math.Direction;
import richiesams.enderio.reforged.api.conduits.Conduit;

public record ConduitConnection(
        Conduit conduit,
        Direction direction,
        Boolean terminated
) {
}
