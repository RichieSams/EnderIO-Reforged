package richiesams.enderio.reforged.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(FireBlock.class)
public interface MixinFireBlockInvoker {
    @Invoker("isRainingAround")
    boolean invokeIsRainingAround(World world, BlockPos pos);

    @Invoker("getFireTickDelay")
    static int invokeGetFireTickDelay(Random random) {
        throw new AssertionError();
    }

    @Invoker("areBlocksAroundFlammable")
    boolean invokeAreBlocksAroundFlammable(BlockView world, BlockPos pos);

    @Invoker("trySpreadingFire")
    void invokeTrySpreadingFire(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge);

    @Invoker("isFlammable")
    boolean invokeIsFlammable(BlockState state);

    @Invoker("getBurnChance")
    int invokeGetBurnChance(WorldView world, BlockPos pos);

    @Invoker("getStateWithAge")
    BlockState invokeGetStateWithAge(WorldAccess world, BlockPos pos, int age);
}
