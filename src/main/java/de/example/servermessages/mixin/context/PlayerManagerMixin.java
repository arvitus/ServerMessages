package de.example.servermessages.mixin.context;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
import java.util.List;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Inject(
        method = "onPlayerConnect",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;equalsIgnoreCase(Ljava/lang/String;)Z"
        )
    )
    private void setJoinContext(
        ClientConnection connection,
        ServerPlayerEntity player,
        ConnectedClientData clientData,
        CallbackInfo ci
    ) {
        CONTEXT_STORE.put("multiplayer.player.joined", PlaceholderContext.of(player));
    }

    @Inject(
        method = "checkCanJoin",
        at = {
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
            ),
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                         "Lnet/minecraft/text/MutableText;"
            )
        }
    )
    private void setDisconnectContext(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        CONTEXT_STORE.put("multiplayer.disconnect", PlaceholderContext.of(profile, this.server));
    }

    @Inject(method = "checkCanJoin", at = @At("RETURN"))
    private void resetDisconnectContext(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        CONTEXT_STORE.pop("multiplayer.disconnect");
    }

    @Inject(
        method = "disconnectAllPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
        )
    )
    private void setServerStopContext(CallbackInfo ci, @Local int i) {
        ServerPlayerEntity player = this.players.get(i);
        CONTEXT_STORE.put("multiplayer.disconnect.server_shutdown", PlaceholderContext.of(player));
    }
}

