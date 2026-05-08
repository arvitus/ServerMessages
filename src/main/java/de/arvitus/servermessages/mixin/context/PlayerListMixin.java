package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @WrapMethod(
        method = "placeNewPlayer"
    )
    private void setJoinContext(
        Connection connection, ServerPlayer player, CommonListenerCookie cookie, Operation<Void> original
    ) {
        ServerMessages.withContext(
            ServerPlaceholderContext.of(player),
            () -> original.call(connection, player, cookie)
        );
    }
}