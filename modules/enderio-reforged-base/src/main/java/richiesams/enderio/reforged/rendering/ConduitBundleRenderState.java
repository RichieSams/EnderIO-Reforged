package richiesams.enderio.reforged.rendering;

import richiesams.enderio.reforged.api.conduits.Conduit;

import java.util.List;

public record ConduitBundleRenderState(
        List<Conduit> conduits,
        List<ConduitConnection> connections
) {
}

