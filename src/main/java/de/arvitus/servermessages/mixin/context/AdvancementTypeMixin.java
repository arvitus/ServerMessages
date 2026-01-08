package de.arvitus.servermessages.mixin.context;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.arvitus.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(AdvancementType.class)
public abstract class AdvancementTypeMixin {
    @Inject(method = "createAnnouncement", at = @At("HEAD"))
    private void setAdvancementContext(
        AdvancementHolder advancementEntry,
        ServerPlayer player,
        CallbackInfoReturnable<MutableComponent> cir
    ) {
        CONTEXT_STORE.put("chat.type.advancement", PlaceholderContext.of(player));
    }
}
