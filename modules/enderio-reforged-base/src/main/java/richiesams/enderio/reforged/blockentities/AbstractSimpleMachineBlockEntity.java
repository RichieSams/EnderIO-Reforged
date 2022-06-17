package richiesams.enderio.reforged.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import richiesams.enderio.reforged.blocks.MachineBlock;
import richiesams.enderio.reforged.screens.BuiltScreenHandlerProvider;

public abstract class AbstractSimpleMachineBlockEntity extends BlockEntity implements BuiltScreenHandlerProvider {
    protected int progress = 0;
    protected int progressTotal = 0;

    public final EnderIOInventory inputsInventory;
    protected final EnderIOInventory outputsInventory;

    public AbstractSimpleMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inputsSize, int outputsSize) {
        super(type, pos, state);

        inputsInventory = createInventory(this, inputsSize);
        outputsInventory = createInventory(this, outputsSize);
    }

    protected static EnderIOInventory createInventory(AbstractSimpleMachineBlockEntity entity, int size) {
        return new EnderIOInventory(size) {
            @Override
            public void markDirty() {
                entity.markDirty();
            }
        };
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getInt("Progress");
        progressTotal = nbt.getInt("ProgressTotal");
        inputsInventory.readNbtList(nbt.getList("Inputs", 10));
        outputsInventory.readNbtList(nbt.getList("Outputs", 10));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Progress", progress);
        nbt.putInt("ProgressTotal", progressTotal);
        nbt.put("Inputs", inputsInventory.toNbtList());
        nbt.put("Outputs", outputsInventory.toNbtList());
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    protected void updateState(boolean lit) {
        Block block = getWorld().getBlockState(pos).getBlock();

        if (block instanceof MachineBlock base) {
            base.setLit(lit, world, pos);
        }
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
    }

    public int getProgress() {
        return progress;
    }

    public int getProgressTotal() {
        return progressTotal;
    }

    public void onBlockDestroyed(World world, BlockPos pos) {
        ItemScatterer.spawn(world, pos, inputsInventory);
        ItemScatterer.spawn(world, pos, outputsInventory);
    }
}
