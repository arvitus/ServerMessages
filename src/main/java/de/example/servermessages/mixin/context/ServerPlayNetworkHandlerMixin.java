package de.example.servermessages.mixin.context;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "cleanUp", at = @At("HEAD"))
    private void setLeaveContext(CallbackInfo ci) {
        CONTEXT_STORE.put("multiplayer.player.left", PlaceholderContext.of(this.player));
    }
}
