package net.minecraft.events;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.util.IdentifiedEvent;

public final class FireEvents {

    public static IdentifiedEvent<FireExtinguished> FIRE_EXTINGUISHED;

    public static IdentifiedEvent<FireDied> FIRE_DIED;

    public static void registerEvents() {
        FIRE_EXTINGUISHED = new IdentifiedEvent<>(
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "fire_extinguished_event"),
                FireExtinguished.class,
                callbacks -> ((world, pos) -> {
                    for (FireExtinguished callback : callbacks) {
                        callback.onFireExtinguished(world, pos);
                    }
                }));
        FIRE_DIED = new IdentifiedEvent<>(
                new Identifier(EnderIOReforgedBaseMod.MOD_ID, "fire_died_event"),
                FireDied.class,
                callbacks -> ((world, pos) -> {
                    for (FireDied callback : callbacks) {
                        callback.onFireDied(world, pos);
                    }
                }));
    }


    public interface FireExtinguished {
        void onFireExtinguished(World world, BlockPos pos);
    }

    public interface FireDied {
        void onFireDied(World world, BlockPos pos);
    }
}
