package richiesams.enderio.reforged.rendering;

import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;

public record ConduitShape(
        List<Box> cores,
        List<Pair<Direction, Box>> connections
) {
}
