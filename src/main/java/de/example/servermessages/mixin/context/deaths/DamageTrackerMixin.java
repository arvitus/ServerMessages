package de.example.servermessages.mixin.context.deaths;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(method = "getAttackedFallDeathMessage", at = @At("HEAD"))
    private void setAttackedFallDeathContext(
        Entity attacker,
        Text attackerDisplayName,
        String itemDeathTranslationKey,
        String deathTranslationKey,
        CallbackInfoReturnable<Text> cir
    ) {
        CONTEXT_STORE.put("death", PlaceholderContext.of(this.entity));
    }

    @Inject(
        method = "getFallDeathMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/text/MutableText;"
        )
    )
    private void setFallDeathContext(DamageRecord damageRecord, Entity attacker, CallbackInfoReturnable<Text> cir) {
        CONTEXT_STORE.put("death.fell", PlaceholderContext.of(this.entity));
    }

    @Inject(
        method = "getDeathMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/text/MutableText;"
        )
    )
    private void setDeathContext(CallbackInfoReturnable<Text> cir) {
        CONTEXT_STORE.put("death.attack", PlaceholderContext.of(this.entity));
    }
}
