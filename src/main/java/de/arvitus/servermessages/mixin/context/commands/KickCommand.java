package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(net.minecraft.server.commands.KickCommand.class)
public abstract class KickCommand {
    @Unique
    private static boolean withoutReason = false;

    @Inject(method = "method_13409", at = @At("HEAD"))
    private static void setType(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        withoutReason = true;
    }

    @Inject(
        method = "kickPlayers",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/level/ServerPlayer;" +
                     "connection:Lnet/minecraft/server/network/ServerGamePacketListenerImpl;",
            opcode = Opcodes.GETFIELD)
    )
    private static void setKickContext(
        CommandSourceStack source,
        Collection<ServerPlayer> targets,
        Component reason,
        CallbackInfoReturnable<Integer> cir,
        @Local ServerPlayer player,
        @Local(argsOnly = true) LocalRef<Component> reasonRef
    ) {
        PlaceholderContext context = PlaceholderContext.of(player);
        CONTEXT_STORE.put("commands.kick.success", context);
        if (!withoutReason) return;
        CONTEXT_STORE.put("multiplayer.disconnect.kicked", context);
        reasonRef.set(Component.translatable("multiplayer.disconnect.kicked"));
    }

    @Inject(method = "kickPlayers", at = @At("RETURN"))
    private static void resetType(
        CommandSourceStack source,
        Collection<ServerPlayer> targets,
        Component reason,
        CallbackInfoReturnable<Integer> cir
    ) {
        withoutReason = false;
    }
}
