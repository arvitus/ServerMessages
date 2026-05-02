package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @WrapOperation(
        method = "removePlayerFromWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;")
    )
    private MutableComponent replaceLeaveMessage(String key, Object[] args, Operation<MutableComponent> original) {
        var component = original.call(key, args);
        return component.servermessages$parse(ServerPlaceholderContext.of(player));
    }
}
