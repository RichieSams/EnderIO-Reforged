package net.minecraft;

import net.minecraft.events.FireEvents;

public class ExtendedVanilla {
    public static void onInitialize() {
        FireEvents.registerEvents();
    }
}
