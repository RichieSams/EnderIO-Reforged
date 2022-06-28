package richiesams.enderio.reforged.items;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.api.conduits.Conduit;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;
import richiesams.enderio.reforged.events.ConduitAddEventHandler;

public class ConduitItem extends Item {
    private final Conduit conduit;

    public ConduitItem(Conduit conduit, Settings settings) {
        super(settings);

        this.conduit = conduit;
    }

    public Conduit getConduit() {
        return conduit;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            return ActionResult.CONSUME;
        }

        BlockPos pos = context.getBlockPos();

        // First we check if the block we hit is a ConduitBundle, and if we can add to it
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConduitBundleBlockEntity conduitBundleBlockEntity) {
            // See if we can add a conduit
            if (conduitBundleBlockEntity.canAddConduit(conduit)) {
                // Send a packet to do the actual work on the server
                ConduitAddEventHandler.sendConduitAddPacket(pos, context.getHand());
                return ActionResult.SUCCESS;
            }
        }

        // Next check if the placement position is a ConduitBundle
        // (AKA, we clicked through the empty space of a ConduitBundle to the block behind)
        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
        pos = itemPlacementContext.getBlockPos();
        blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConduitBundleBlockEntity conduitBundleBlockEntity) {
            // See if we can add a conduit
            if (conduitBundleBlockEntity.canAddConduit(conduit)) {
                // Send a packet to do the actual work on the server
                ConduitAddEventHandler.sendConduitAddPacket(pos, context.getHand());
                return ActionResult.SUCCESS;
            }
        }

        // If not, then we check if we can place a new conduit
        if (itemPlacementContext.canPlace()) {
            // Send a packet to do the actual work on the server
            ConduitAddEventHandler.sendConduitAddPacket(pos, context.getHand());
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
