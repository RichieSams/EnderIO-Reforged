package richiesams.enderio.reforged.rendering;

import richiesams.enderio.reforged.api.conduits.Conduit;

import java.util.List;

public record ConduitBundleRenderState(
        List<ConduitRenderState> conduitRenderStates
) {
    public record ConduitRenderState(
            Conduit conduit,
            ConduitShape shape
    ) {
    }
}

