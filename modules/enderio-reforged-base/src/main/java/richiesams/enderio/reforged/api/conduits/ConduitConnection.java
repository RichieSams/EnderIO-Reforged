package richiesams.enderio.reforged.api.conduits;

public record ConduitConnection(
        boolean terminated,
        boolean input,
        boolean output
) {
}
