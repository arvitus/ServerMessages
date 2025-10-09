package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(
        method = "kickNonWhitelistedPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
        )
    )
    private void setWhitelistContext(CallbackInfo ci, @Local ServerPlayerEntity player) {
        CONTEXT_STORE.put("multiplayer.disconnect.not_whitelisted", PlaceholderContext.of(player));
    }
}
