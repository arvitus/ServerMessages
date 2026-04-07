package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
import java.util.List;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;equalsIgnoreCase(Ljava/lang/String;)Z"
        )
    )
    private void setJoinContext(
        Connection connection,
        ServerPlayer player,
        CommonListenerCookie clientData,
        CallbackInfo ci
    ) {
        CONTEXT_STORE.put("multiplayer.player.joined", ServerPlaceholderContext.of(player));
    }

    @Inject(
        method = "canPlayerLogin",
        at = {
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)" +
                         "Lnet/minecraft/network/chat/MutableComponent;"
            ),
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                         "Lnet/minecraft/network/chat/MutableComponent;"
            )
        }
    )
    private void setDisconnectContext(
        SocketAddress address,
        NameAndId configEntry,
        CallbackInfoReturnable<Component> cir
    ) {
        GameProfile profile = new GameProfile(configEntry.id(), configEntry.name());
        CONTEXT_STORE.put("multiplayer.disconnect", ServerPlaceholderContext.of(profile, this.server));
    }

    @Inject(method = "canPlayerLogin", at = @At("RETURN"))
    private void resetDisconnectContext(
        SocketAddress address,
        NameAndId configEntry,
        CallbackInfoReturnable<Component> cir
    ) {
        CONTEXT_STORE.pop("multiplayer.disconnect");
    }

    @Inject(
        method = "removeAll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private void setServerStopContext(CallbackInfo ci, @Local int i) {
        ServerPlayer player = this.players.get(i);
        CONTEXT_STORE.put("multiplayer.disconnect.server_shutdown", ServerPlaceholderContext.of(player));
    }
}