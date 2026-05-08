package de.arvitus.servermessages.mixin.context.deaths;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @WrapMethod(
        method = "die"
    )
    private void setDeathContext(DamageSource source, Operation<Void> original) {
        ServerMessages.withContext(
            ServerPlaceholderContext.of((ServerPlayer) (Object) this),
            () -> original.call(source)
        );
    }
}
