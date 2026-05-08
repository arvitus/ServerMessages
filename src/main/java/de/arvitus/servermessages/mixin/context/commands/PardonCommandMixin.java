package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.players.NameAndId;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PardonCommand.class)
public class PardonCommandMixin {
    @WrapMethod(method = "lambda$pardonPlayers$0")
    private static Component setFeedbackContext(
        NameAndId player, Operation<Component> original
    ) {
        if (ServerMessages.SERVER == null) return original.call(player);
        var serverPlayer = ServerMessages.SERVER.getPlayerList().getPlayer(player.id());
        if (serverPlayer == null) return original.call(player);
        return ServerMessages.withContext(
            ServerPlaceholderContext.of(serverPlayer),
            () -> original.call(player)
        );
    }
}
