package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.context.CommandContext;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KickCommand.class)
public abstract class KickCommandMixin {
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

    @WrapMethod(method = "lambda$kickPlayers$0")
    private static Component setFeedbackContext(ServerPlayer player, Component reason, Operation<Component> original) {
        return ServerMessages.withContext(ServerPlaceholderContext.of(player), original::call);
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
        Operation<Void> original,
        @Local(argsOnly = true) LocalRef<Component> reasonRef
    ) {
        var contents = hasReason
            ? new TranslatableContents("multiplayer.disconnect.kicked.reason", "%s", new Component[]{component})
            : component.servermessages$getOriginal();
        component = ServerMessages.parseWithContext(ServerPlaceholderContext.of(instance.getPlayer()), contents);
        original.call(instance, component);
        if (!hasReason) reasonRef.set(component);
    }
}
