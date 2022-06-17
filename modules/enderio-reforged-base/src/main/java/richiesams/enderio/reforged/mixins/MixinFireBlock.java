package richiesams.enderio.reforged.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.events.FireEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

import static net.minecraft.block.FireBlock.AGE;

@Mixin(FireBlock.class)
public abstract class MixinFireBlock implements MixinFireBlockInvoker {
    /**
     * @reason This is intended to be the same code as FireBlock::scheduledTick
     * The only change is to generate custom events when the fire is extinguished
     * "naturally", by timing out.
     * <p>
     * The only other changes to the code are just Mixin nonsense, like doing
     * a `(FireBlock)(Object)this` cast, and using invoke*() methods instead
     * of the real ones
     * @author RichieSams
     */
    @Overwrite()
    public void scheduledTick(BlockState state, @NotNull ServerWorld world, BlockPos pos, Random random) {
        world.createAndScheduleBlockTick(pos, (FireBlock) (Object) this, MixinFireBlockInvoker.invokeGetFireTickDelay(world.random));

        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            if (!state.canPlaceAt(world, pos)) {
                world.removeBlock(pos, false);
            }

            BlockState blockState = world.getBlockState(pos.down());
            boolean bl = blockState.isIn(world.getDimension().getInfiniburnBlocks());
            int i = (Integer) state.get(AGE);
            if (!bl && world.isRaining() && this.invokeIsRainingAround(world, pos) && random.nextFloat() < 0.2F + (float) i * 0.03F) {
                world.removeBlock(pos, false);
            } else {
                int j = Math.min(15, i + random.nextInt(3) / 2);
                if (i != j) {
                    state = (BlockState) state.with(AGE, j);
                    world.setBlockState(pos, state, 4);
                }

                if (!bl) {
                    if (!this.invokeAreBlocksAroundFlammable(world, pos)) {
                        BlockPos blockPos = pos.down();

                        //-----------------------------------------------------------------------------
                        // Start modified code
                        //
                        // The original code was a single if statement for both checking the block
                        // above us AND if we hit the time limit (i > 3)
                        // We just split that into two if checks, so we can fire an event for both
                        //-----------------------------------------------------------------------------
                        if (!world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP)) {
                            world.removeBlock(pos, false);
                            FireEvents.FIRE_EXTINGUISHED.event.invoker().onFireExtinguished(world, pos);
                        } else if (i > 3) {
                            world.removeBlock(pos, false);
                            FireEvents.FIRE_DIED.event.invoker().onFireDied(world, pos);
                        }
                        //-----------------------------------------------------------------------------
                        // End modified code
                        //-----------------------------------------------------------------------------

                        return;
                    }

                    if (i == 15 && random.nextInt(4) == 0 && !this.invokeIsFlammable(world.getBlockState(pos.down()))) {
                        world.removeBlock(pos, false);
                        //-----------------------------------------------------------------------------
                        // Start modified code
                        //-----------------------------------------------------------------------------
                        FireEvents.FIRE_DIED.event.invoker().onFireDied(world, pos);
                        //-----------------------------------------------------------------------------
                        // End modified code
                        //-----------------------------------------------------------------------------
                        return;
                    }
                }

                boolean bl2 = world.hasHighHumidity(pos);
                int k = bl2 ? -50 : 0;
                this.invokeTrySpreadingFire(world, pos.east(), 300 + k, random, i);
                this.invokeTrySpreadingFire(world, pos.west(), 300 + k, random, i);
                this.invokeTrySpreadingFire(world, pos.down(), 250 + k, random, i);
                this.invokeTrySpreadingFire(world, pos.up(), 250 + k, random, i);
                this.invokeTrySpreadingFire(world, pos.north(), 300 + k, random, i);
                this.invokeTrySpreadingFire(world, pos.south(), 300 + k, random, i);
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                for (int l = -1; l <= 1; ++l) {
                    for (int m = -1; m <= 1; ++m) {
                        for (int n = -1; n <= 4; ++n) {
                            if (l != 0 || n != 0 || m != 0) {
                                int o = 100;
                                if (n > 1) {
                                    o += (n - 1) * 100;
                                }

                                mutable.set(pos, l, n, m);
                                int p = this.invokeGetBurnChance(world, mutable);
                                if (p > 0) {
                                    int q = (p + 40 + world.getDifficulty().getId() * 7) / (i + 30);
                                    if (bl2) {
                                        q /= 2;
                                    }

                                    if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.invokeIsRainingAround(world, mutable))) {
                                        int r = Math.min(15, i + random.nextInt(5) / 4);
                                        world.setBlockState(mutable, this.invokeGetStateWithAge(world, mutable, r), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

