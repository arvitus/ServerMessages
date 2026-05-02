package de.arvitus.servermessages.mixin;

import de.arvitus.servermessages.interfaces.IParseable;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComponentContents.class)
public interface ComponentContentsMixin extends IParseable {
    @Override
    default @Nullable ServerPlaceholderContext servermessages$getContext() {
        return null;
    }

    @Override
    default void servermessages$setContext(ServerPlaceholderContext context) {}

    @Override
    default boolean servermessages$canParse() {
        return false;
    }

    @Override
    default MutableComponent servermessages$parse(@Nullable ServerPlaceholderContext context) {
        return MutableComponent.create((ComponentContents) this);
    }
}
