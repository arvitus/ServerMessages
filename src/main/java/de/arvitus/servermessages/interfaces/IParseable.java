package de.arvitus.servermessages.interfaces;

import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import static de.arvitus.servermessages.ServerMessages.SERVER;

public interface IParseable {
    default @Nullable ServerPlaceholderContext servermessages$getContext() {
        throw new RuntimeException("Method not implemented");
    }

    default void servermessages$setContext(ServerPlaceholderContext context) {
        throw new RuntimeException("Method not implemented");
    }

    default MutableComponent servermessages$parse() {
        if (servermessages$getContext() != null) return servermessages$parse(servermessages$getContext());
        if (SERVER != null) return servermessages$parse(ServerPlaceholderContext.of(SERVER));
        return servermessages$parse(null);
    }

    default MutableComponent servermessages$parse(@Nullable ServerPlaceholderContext context) {
        throw new RuntimeException("Method not Implemented");
    }

    default boolean servermessages$canParse() {
        throw new RuntimeException("Method not Implemented");
    }

    default @Nullable MutableComponent servermessages$getOriginal() {
        return null;
    }

    default void servermessages$setOriginal(@Nullable MutableComponent original) {}

    default boolean servermessages$hasOriginal() {
        return servermessages$getOriginal() != null;
    }
}
