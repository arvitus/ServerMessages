package de.arvitus.servermessages.interfaces;

import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public interface IMutableComponent {
    default @Nullable MutableComponent servermessages$getOriginal() {
        return null;
    }

    default void servermessages$setOriginal(@Nullable MutableComponent original) {}

    default boolean servermessages$hasOriginal() {
        return servermessages$getOriginal() != null;
    }
}
