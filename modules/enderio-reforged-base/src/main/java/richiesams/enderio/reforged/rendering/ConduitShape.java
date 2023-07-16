package richiesams.enderio.reforged.rendering;

import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import richiesams.enderio.reforged.api.conduits.ConduitEntity;

import java.util.List;

public record ConduitShape(
        ConduitEntity entity,
        List<Box> cores,
        List<Pair<Direction, Box>> connections
) {
}
