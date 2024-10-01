package de.example.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(net.minecraft.server.command.KickCommand.class)
public abstract class KickCommand {
    @Unique
    private static boolean withoutReason = false;

    @Inject(method = "method_13409", at = @At("HEAD"))
    private static void setType(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir) {
        withoutReason = true;
    }

    @Inject(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;" +
                     "networkHandler:Lnet/minecraft/server/network/ServerPlayNetworkHandler;"
        )
    )
    private static void setKickContext(
        ServerCommandSource source,
        Collection<ServerPlayerEntity> targets,
        Text reason,
        CallbackInfoReturnable<Integer> cir,
        @Local ServerPlayerEntity player,
        @Local(argsOnly = true) LocalRef<Text> reasonRef
    ) {
        PlaceholderContext context = PlaceholderContext.of(player);
        CONTEXT_STORE.put("commands.kick.success", context);
        if (!withoutReason) return;
        CONTEXT_STORE.put("multiplayer.disconnect.kicked", context);
        reasonRef.set(Text.translatable("multiplayer.disconnect.kicked"));
    }

    @Inject(method = "execute", at = @At("RETURN"))
    private static void resetType(
        ServerCommandSource source,
        Collection<ServerPlayerEntity> targets,
        Text reason,
        CallbackInfoReturnable<Integer> cir
    ) {
        withoutReason = false;
    }
}
