package de.example.servermessages.mixin.context;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.example.servermessages.ServerMessages.CONTEXT_STORE;

@Mixin(AdvancementFrame.class)
public abstract class AdvancementFrameMixin {
    @Inject(method = "getChatAnnouncementText", at = @At("HEAD"))
    private void setAdvancementContext(
        AdvancementEntry advancementEntry,
        ServerPlayerEntity player,
        CallbackInfoReturnable<MutableText> cir
    ) {
        CONTEXT_STORE.put("chat.type.advancement", PlaceholderContext.of(player));
    }
}
