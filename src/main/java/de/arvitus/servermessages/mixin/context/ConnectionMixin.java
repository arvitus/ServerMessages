package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.fabric.api.networking.v1.context.PacketContextProvider;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @WrapMethod(
        method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V"
    )
    private void setDisconnectionContext(DisconnectionDetails details, Operation<Void> original) {
        var newReason = replaceTranslatable(details.reason());
        if (newReason != details.reason())
            details = new DisconnectionDetails(
                newReason,
                details.report(),
                details.bugReportLink()
            );

        original.call(details);
    }

    @WrapMethod(
        method = "sendPacket"
    )
    private void setPacketContext(
        Packet<?> packet,
        @Nullable ChannelFutureListener listener,
        boolean flush,
        Operation<Void> original
    ) {
        var component = switch (packet) {
            case ClientboundDisconnectPacket p -> p.reason();
            case ClientboundLoginDisconnectPacket p -> p.reason();
            default -> null;
        };
        if (component == null || !component.getContents().servermessages$canParse()) {
            original.call(packet, listener, flush);
            return;
        }

        var newComponent = replaceTranslatable(component);
        packet = switch (packet) {
            case ClientboundDisconnectPacket _ -> new ClientboundDisconnectPacket(newComponent);
            case ClientboundLoginDisconnectPacket _ -> new ClientboundLoginDisconnectPacket(newComponent);
            default -> null;
        };

        original.call(packet, listener, flush);
    }

    @Unique
    private Component replaceTranslatable(Component component) {
        var original = component.servermessages$getOriginal();
        if (original == null || !original.servermessages$canParse()) return component;

        var packetContext = ((PacketContextProvider) this).getPacketContext();
        var gameProfile = packetContext.get(PacketContext.GAME_PROFILE);
        var server = ServerMessages.SERVER;

        if (server == null) return component;
        if (gameProfile == null) return ServerMessages.parseWithContext(ServerPlaceholderContext.of(server), original);

        var player = server.getPlayerList().getPlayer(gameProfile.id());
        if (player != null) return ServerMessages.parseWithContext(ServerPlaceholderContext.of(player), original);

        return ServerMessages.parseWithContext(ServerPlaceholderContext.of(gameProfile, server), original);
    }
}