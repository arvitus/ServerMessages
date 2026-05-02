package de.arvitus.servermessages.mixin.context.deaths;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
    @WrapMethod(method = "getLocalizedDeathMessage")
    private Component setDeathContext(LivingEntity victim, Operation<Component> original) {
        var component = (MutableComponent) original.call(victim);
        return component.servermessages$parse(ServerPlaceholderContext.of(victim));
    }
}
