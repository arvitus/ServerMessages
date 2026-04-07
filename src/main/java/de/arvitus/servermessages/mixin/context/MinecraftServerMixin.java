package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(
        method = "kickUnlistedPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private void setWhitelistContext(CallbackInfo ci, @Local ServerPlayer player) {
        CONTEXT_STORE.put("multiplayer.disconnect.not_whitelisted", ServerPlaceholderContext.of(player));
    }
}
