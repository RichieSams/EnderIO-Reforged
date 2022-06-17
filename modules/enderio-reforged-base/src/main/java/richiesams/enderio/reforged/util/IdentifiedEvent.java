package richiesams.enderio.reforged.util;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class IdentifiedEvent<T> {
    public final Identifier id;
    public final Event<T> event;

    public IdentifiedEvent(Identifier identifier, Class<? super T> type, Function<T[], T> invokerFactory) {
        this.id = identifier;
        this.event = EventFactory.createArrayBacked(type, invokerFactory);
    }
}
