package de.arvitus.servermessages.mixin.context;

import com.google.common.net.InetAddresses;
import eu.pb4.placeholders.api.PlaceholderContext;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;
import static de.arvitus.servermessages.ServerMessages.SERVER;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Shadow
    private SocketAddress address;

    @Inject(
        method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;",
            ordinal = 0
        )
    )
    private void setDisconnectContext(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (SERVER == null) return;

        String ip = this.address instanceof InetSocketAddress inetSocketAddress
            ? InetAddresses.toAddrString(inetSocketAddress.getAddress())
            : "<unknown>";

        List<ServerPlayer> players = SERVER.getPlayerList().getPlayersWithAddress(ip);
        if (players.isEmpty()) return;

        CONTEXT_STORE.put("multiplayer.disconnect.server_shutdown", PlaceholderContext.of(players.getFirst()));
    }
}
