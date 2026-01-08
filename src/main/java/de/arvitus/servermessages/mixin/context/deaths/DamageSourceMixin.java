package de.arvitus.servermessages.mixin.context.deaths;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"))
    private void setDeathContext(LivingEntity killed, CallbackInfoReturnable<Component> cir) {
        CONTEXT_STORE.put("death.attack", PlaceholderContext.of(killed));
    }
}
