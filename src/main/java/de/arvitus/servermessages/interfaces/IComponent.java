package de.arvitus.servermessages.interfaces;

import net.minecraft.network.chat.ComponentContents;
import org.jetbrains.annotations.Nullable;

public interface IComponent {
    default @Nullable ComponentContents servermessages$getOriginal() {
        return null;
    }

    default void servermessages$setOriginal(@Nullable ComponentContents original) {}

    default boolean servermessages$hasOriginal() {
        return servermessages$getOriginal() != null;
    }
}
