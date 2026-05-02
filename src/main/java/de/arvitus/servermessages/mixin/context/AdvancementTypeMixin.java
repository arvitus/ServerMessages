package de.arvitus.servermessages.mixin.context;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AdvancementType.class)
public abstract class AdvancementTypeMixin {
    @WrapMethod(
        method = "createAnnouncement"
    )
    private MutableComponent replaceAnnouncementMessage(
        AdvancementHolder holder,
        ServerPlayer player,
        Operation<MutableComponent> original
    ) {
        var component = original.call(holder, player);
        return component.servermessages$parse(ServerPlaceholderContext.of(player));
    }
}
