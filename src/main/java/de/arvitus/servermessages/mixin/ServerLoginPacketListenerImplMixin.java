package de.arvitus.servermessages.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.impl.networking.context.PacketContextImpl;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplMixin {
    @Shadow
    @Final
    private Connection connection;

    @WrapMethod(
        method = "startClientVerification"
    )
    private void setGameProfile(GameProfile profile, Operation<Void> original) {
        original.call(profile);
        connection.getPacketContext().set(PacketContextImpl.GAME_PROFILE, profile);
    }
}
