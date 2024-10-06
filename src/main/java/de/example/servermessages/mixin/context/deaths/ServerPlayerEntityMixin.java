package de.example.servermessages.mixin.context.deaths;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(
        method = "method_14223",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/text/MutableText;"
        )
    )
    private void setExceptionalDeathContext(Text text, CallbackInfoReturnable<Packet<?>> cir) {
        CONTEXT_STORE.put("death.attack", PlaceholderContext.of((ServerPlayerEntity) (Object) this));
    }
}
