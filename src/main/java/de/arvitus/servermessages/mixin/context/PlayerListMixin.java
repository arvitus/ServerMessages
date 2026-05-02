package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @WrapOperation(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage" +
                     "(Lnet/minecraft/network/chat/Component;Z)V")
    )
    private void replaceJoinMessage(
        PlayerList instance,
        Component message,
        boolean overlay,
        Operation<Void> original,
        @Local(argsOnly = true) ServerPlayer player
    ) {
        var component = ((MutableComponent) message).servermessages$parse(ServerPlaceholderContext.of(player));
        original.call(instance, component, overlay);
    }
}