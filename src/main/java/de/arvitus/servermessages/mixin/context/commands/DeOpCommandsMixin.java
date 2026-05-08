package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.players.NameAndId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(DeOpCommands.class)
public class DeOpCommandsMixin {
    @WrapOperation(method = "deopPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/CommandSourceStack;sendSuccess(Ljava/util/function/Supplier;Z)V"
        )
    )
    private static void setFeedbackContext(
        CommandSourceStack instance,
        Supplier<Component> messageSupplier,
        boolean broadcast,
        Operation<Void> original,
        @Local NameAndId nameAndId
    ) {
        if (ServerMessages.SERVER != null) {
            var player = ServerMessages.SERVER.getPlayerList().getPlayer(nameAndId.id());
            if (player != null) {
                ServerMessages.withContext(
                    ServerPlaceholderContext.of(player),
                    () -> original.call(instance, messageSupplier, broadcast)
                );
                return;
            }
        }
        original.call(instance, messageSupplier, broadcast);
    }
}
