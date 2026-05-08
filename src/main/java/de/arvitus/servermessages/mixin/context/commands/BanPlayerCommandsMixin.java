package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BanPlayerCommands.class)
public class BanPlayerCommandsMixin {
    @WrapMethod(method = "lambda$banPlayers$0")
    private static Component setFeedbackContext(
        NameAndId player,
        UserBanListEntry entry,
        Operation<Component> original
    ) {
        if (ServerMessages.SERVER == null) return original.call(player, entry);
        var serverPlayer = ServerMessages.SERVER.getPlayerList().getPlayer(player.id());
        if (serverPlayer == null) return original.call(player, entry);
        return ServerMessages.withContext(
            ServerPlaceholderContext.of(serverPlayer),
            () -> original.call(player, entry)
        );
    }
}
