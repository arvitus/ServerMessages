package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.server.commands.KickCommand.class)
public abstract class KickCommand {
    @Unique
    private static boolean hasReason = false;

    @Inject(method = "lambda$register$0", at = @At("HEAD"))
    private static void setNoReason(CommandContext<CommandSourceStack> c, CallbackInfoReturnable<Integer> cir) {
        hasReason = false;
    }

    @Inject(method = "lambda$register$1", at = @At("HEAD"))
    private static void setReason(CommandContext<CommandSourceStack> c, CallbackInfoReturnable<Integer> cir) {
        hasReason = true;
    }

    @WrapOperation(
        method = "lambda$kickPlayers$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;")
    )
    private static MutableComponent setFeedbackContext(
        String key,
        Object[] args,
        Operation<MutableComponent> original,
        @Local ServerPlayer player
    ) {
        var component = original.call(key, args);
        component.servermessages$setContext(ServerPlaceholderContext.of(player));
        return component;
    }

    @WrapOperation(
        method = "kickPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect" +
                     "(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private static void replaceKickReason(
        ServerGamePacketListenerImpl instance,
        Component component,
        Operation<Void> original
    ) {
        component = hasReason ?
            Component.translatableWithFallback(
                "multiplayer.disconnect.kicked.reason",
                "%s",
                component
            )
            : component;
        original.call(instance, component);
    }
}
