package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.context.CommandContext;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

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
        method = "kickPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect" +
                     "(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private static void setKickContext(
        ServerGamePacketListenerImpl instance,
        Component component,
        Operation<Void> original,
        @Local ServerPlayer player,
        @Local(argsOnly = true) LocalRef<Component> reasonRef
    ) {
        ServerPlaceholderContext context = ServerPlaceholderContext.of(player);
        CONTEXT_STORE.put("commands.kick.success", context);

        CONTEXT_STORE.put("multiplayer.disconnect.kicked", context);
        if (!hasReason) {
            original.call(instance, Component.translatable("multiplayer.disconnect.kicked"));
            ServerMessages.withDisabled(() -> reasonRef.set(Component.translatable("multiplayer.disconnect.kicked")));
        } else original.call(
            instance,
            Component.translatableWithFallback("multiplayer.disconnect.kicked.reason", "%s", component)
        );
    }
}
