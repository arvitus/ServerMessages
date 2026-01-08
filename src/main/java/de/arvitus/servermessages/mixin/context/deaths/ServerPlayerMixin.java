package de.arvitus.servermessages.mixin.context.deaths;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(
        method = "method_14223",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private void setExceptionalDeathContext(Component text, CallbackInfoReturnable<Packet<?>> cir) {
        CONTEXT_STORE.put("death.attack", PlaceholderContext.of((ServerPlayer) (Object) this));
    }
}
