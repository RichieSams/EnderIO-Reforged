package richiesams.enderio.reforged.screens;

import net.minecraft.entity.player.PlayerEntity;

public interface BuiltScreenHandlerProvider {
    BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player);
}
