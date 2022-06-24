package richiesams.enderio.reforged.blockentities;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
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
import richiesams.enderio.reforged.util.EnderIOInventory;
import team.reborn.energy.api.base.SimpleEnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractSimpleMachineBlockEntity extends BlockEntity implements BuiltScreenHandlerProvider {
    protected int progress = 0;
    protected int progressTotal = 0;

    protected final EnderIOInventory inputsInventory;
    protected final EnderIOInventory outputsInventory;

    public final InventoryStorage InputStorage;
    public final InventoryStorage OutputStorage;
    public SimpleEnergyStorage EnergyStorage;

    public AbstractSimpleMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                            int inputsSize, int outputsSize,
                                            int energyCapacity, int maxEnergyInsertion, int maxEnergyExtraction) {
        super(type, pos, state);

        inputsInventory = createInventory(this, inputsSize);
        outputsInventory = createInventory(this, outputsSize);
        InputStorage = InventoryStorage.of(inputsInventory, null);
        OutputStorage = InventoryStorage.of(outputsInventory, null);

        EnergyStorage = new SimpleEnergyStorage(energyCapacity, maxEnergyInsertion, maxEnergyExtraction) {
            @Override
            protected void onFinalCommit() {
                AbstractSimpleMachineBlockEntity.this.markDirty();
                AbstractSimpleMachineBlockEntity.this.world.updateListeners(
                        AbstractSimpleMachineBlockEntity.this.pos,
                        AbstractSimpleMachineBlockEntity.this.getCachedState(),
                        AbstractSimpleMachineBlockEntity.this.getCachedState(),
                        Block.NOTIFY_LISTENERS);
            }
        };
    }

    protected static EnderIOInventory createInventory(AbstractSimpleMachineBlockEntity entity, int size) {
        return new EnderIOInventory(size) {
            @Override
            public void markDirty() {
                entity.markDirty();
                entity.world.updateListeners(entity.pos, entity.getCachedState(), entity.getCachedState(), Block.NOTIFY_LISTENERS);
            }
        };
    }

    protected static EnderIOInventory createInventory(AbstractSimpleMachineBlockEntity entity, int size, int maxCountPerStack) {
        return new EnderIOInventory(size, maxCountPerStack) {
            @Override
            public void markDirty() {
                entity.markDirty();
                entity.world.updateListeners(entity.pos, entity.getCachedState(), entity.getCachedState(), Block.NOTIFY_LISTENERS);
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
        EnergyStorage.amount = nbt.getLong("Energy");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Progress", progress);
        nbt.putInt("ProgressTotal", progressTotal);
        nbt.put("Inputs", inputsInventory.toNbtList());
        nbt.put("Outputs", outputsInventory.toNbtList());
        nbt.putLong("Energy", EnergyStorage.amount);
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
