package de.example.servermessages.mixin.context;

import com.google.common.net.InetAddresses;
import eu.pb4.placeholders.api.PlaceholderContext;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;
import static de.example.servermessages.ServerMessages.SERVER;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow
    private SocketAddress address;

    @Inject(
        method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;",
            ordinal = 0
        )
    )
    private void setDisconnectContext(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (SERVER == null) return;

        String ip = this.address instanceof InetSocketAddress inetSocketAddress
            ? InetAddresses.toAddrString(inetSocketAddress.getAddress())
            : "<unknown>";

        List<ServerPlayerEntity> players = SERVER.getPlayerManager().getPlayersByIp(ip);
        if (players.isEmpty()) return;

        CONTEXT_STORE.put("multiplayer.disconnect.server_shutdown", PlaceholderContext.of(players.getFirst()));
    }
}
