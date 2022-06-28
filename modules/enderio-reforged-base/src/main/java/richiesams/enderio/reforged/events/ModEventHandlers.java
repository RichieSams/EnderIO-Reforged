package richiesams.enderio.reforged.events;

public class ModEventHandlers {
    public static void registerEventHandlers() {
        WorldCraftingEvents.registerWorldCraftingEvents();
        ConduitAddEventHandler.registerPacketHandling();
    }
}
