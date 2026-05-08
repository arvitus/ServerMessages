package de.arvitus.servermessages.interfaces;

import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public interface IParseable {
    default MutableComponent servermessages$parse(@Nullable ServerPlaceholderContext context) {
        throw new RuntimeException("Method not Implemented");
    }

    default MutableComponent servermessages$parseRaw() {
        throw new RuntimeException("Method not Implemented");
    }

    default boolean servermessages$canParse() {
        throw new RuntimeException("Method not Implemented");
    }
}