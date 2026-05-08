package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.players.NameAndId;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;

@Mixin(DeOpCommands.class)
public class DeOpCommandsMixin {
    @WrapMethod(method = "lambda$deopPlayers$0")
    private static Component setFeedbackContext(
        Collection<NameAndId> players,
        Operation<Component> original,
        @Local NameAndId nameAndId
    ) {
        if (ServerMessages.SERVER == null) return original.call(players);
        var player = ServerMessages.SERVER.getPlayerList().getPlayer(nameAndId.id());
        if (player == null) return original.call(players);
        return ServerMessages.withContext(ServerPlaceholderContext.of(player), () -> original.call(players));
    }
}
