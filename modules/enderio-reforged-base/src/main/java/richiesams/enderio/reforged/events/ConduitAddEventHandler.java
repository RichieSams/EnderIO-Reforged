package richiesams.enderio.reforged.events;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;
import richiesams.enderio.reforged.blockentities.ConduitBundleBlockEntity;
import richiesams.enderio.reforged.blocks.ModBlocks;

public class ConduitAddEventHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static final Identifier CONDUIT_ADD_PACKET = new Identifier(EnderIOReforgedBaseMod.MOD_ID, "conduit_add_packet");

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;

        // Execute on the main thread
        server.execute(() -> {
            World world = player.world;

            BlockState targetState = world.getBlockState(pos);
            // If the block doesn't exist yet, create it
            if (targetState.isAir()) {
                world.setBlockState(pos, ModBlocks.CONDUIT_BUNDLE.getDefaultState());
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundleBlockEntity) {
                conduitBundleBlockEntity.addConduit(player, hand);
            } else {
                throw new RuntimeException("Received a CONDUIT_ADD_PACKET for a non ConduitBundleBlockEntity position");
            }
        });
    }

    public static void sendConduitAddPacket(BlockPos pos, Hand hand) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeBlockPos(pos);
        buffer.writeBoolean(hand.equals(Hand.MAIN_HAND));

        ClientPlayNetworking.send(CONDUIT_ADD_PACKET, buffer);
    }

    static public void registerPacketHandling() {
        ConduitAddEventHandler handler = new ConduitAddEventHandler();

        ServerPlayNetworking.registerGlobalReceiver(ConduitAddEventHandler.CONDUIT_ADD_PACKET, handler);
    }
}
