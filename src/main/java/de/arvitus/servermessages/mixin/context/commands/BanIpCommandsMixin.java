package de.arvitus.servermessages.mixin.context.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanListEntry;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(BanIpCommands.class)
public class BanIpCommandsMixin {
    @WrapMethod(method = "lambda$banIp$0")
    private static Component setFeedbackContext(
        String ip, IpBanListEntry entry, Operation<Component> original, @Local List<ServerPlayer> players
    ) {
        if (players.isEmpty()) return original.call(ip, entry);
        return ServerMessages.withContext(
            ServerPlaceholderContext.of(players.getFirst()),
            () -> original.call(ip, entry)
        );
    }
}
