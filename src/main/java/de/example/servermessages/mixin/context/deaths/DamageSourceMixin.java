package de.example.servermessages.mixin.context.deaths;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
    @Inject(method = "getDeathMessage", at = @At("HEAD"))
    private void setDeathContext(LivingEntity killed, CallbackInfoReturnable<Text> cir) {
        CONTEXT_STORE.put("death.attack", PlaceholderContext.of(killed));
    }
}
