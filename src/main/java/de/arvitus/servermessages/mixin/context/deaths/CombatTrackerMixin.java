package de.arvitus.servermessages.mixin.context.deaths;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(CombatTracker.class)
public abstract class CombatTrackerMixin {
    @Shadow
    @Final
    private LivingEntity mob;

    @Inject(method = "getMessageForAssistedFall", at = @At("HEAD"))
    private void setAttackedFallDeathContext(
        Entity attacker,
        Component attackerDisplayName,
        String itemDeathTranslationKey,
        String deathTranslationKey,
        CallbackInfoReturnable<Component> cir
    ) {
        CONTEXT_STORE.put("death", PlaceholderContext.of(this.mob));
    }

    @Inject(
        method = "getFallMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private void setFallDeathContext(CombatEntry damageRecord, Entity attacker, CallbackInfoReturnable<Component> cir) {
        CONTEXT_STORE.put("death.fell", PlaceholderContext.of(this.mob));
    }

    @Inject(
        method = "getDeathMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)" +
                     "Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private void setDeathContext(CallbackInfoReturnable<Component> cir) {
        CONTEXT_STORE.put("death.attack", PlaceholderContext.of(this.mob));
    }
}
