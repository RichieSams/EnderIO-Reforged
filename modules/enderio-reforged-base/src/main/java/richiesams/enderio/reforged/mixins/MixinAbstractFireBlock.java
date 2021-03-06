package richiesams.enderio.reforged.mixins;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.events.FireEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public class MixinAbstractFireBlock {
    @Inject(method = "onBreak", at = @At("TAIL"))
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (world instanceof ServerWorld serverWorld) {
            FireEvents.FIRE_EXTINGUISHED.event.invoker().onFireExtinguished(serverWorld, pos);
        }
    }
}
