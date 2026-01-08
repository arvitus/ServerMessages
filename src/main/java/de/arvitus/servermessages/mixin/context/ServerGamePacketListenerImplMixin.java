package de.arvitus.servermessages.mixin.context;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "removePlayerFromWorld", at = @At("HEAD"))
    private void setLeaveContext(CallbackInfo ci) {
        CONTEXT_STORE.put("multiplayer.player.left", PlaceholderContext.of(this.player));
    }
}
