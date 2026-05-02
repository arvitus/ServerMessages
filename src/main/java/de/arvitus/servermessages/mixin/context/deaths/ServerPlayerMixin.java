package de.arvitus.servermessages.mixin.context.deaths;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @WrapOperation(
        method = "lambda$die$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private MutableComponent replaceExceptionalDeathMessage(
        String key,
        Object[] args,
        Operation<MutableComponent> original
    ) {
        var component = original.call(key, args);
        return component.servermessages$parse(ServerPlaceholderContext.of((ServerPlayer) (Object) this));
    }
}
