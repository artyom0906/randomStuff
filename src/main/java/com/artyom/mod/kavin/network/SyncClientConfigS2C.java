package com.artyom.mod.kavin.network;

import com.artyom.mod.RandomStuff;
import com.artyom.mod.kavin.init.InventoryTabsServerConfig;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncClientConfigS2C {
    public static final Identifier ID = RandomStuff.identify("sync_client_config_s2c");

    public static void sendToPlayer(ServerPlayerEntity player, boolean doSightChecks) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(doSightChecks);

        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(ID, (context, buf) -> {
            boolean doSightChecks = buf.readBoolean();

            context.getTaskQueue().execute(() -> {
                InventoryTabsServerConfig.doSightChecks = doSightChecks;
            });
        });
    }
}