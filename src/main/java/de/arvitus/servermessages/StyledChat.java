package de.arvitus.servermessages;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.List;

public class StyledChat {
    public static final boolean IS_LOADED = FabricLoader.getInstance().isModLoaded("styledchat");
    public static final List<String> DISABLED_TRANSLATIONS = List.of(
        "chat.type.text",
        "multiplayer.player.joined",
        "multiplayer.player.joined.renamed",
        "multiplayer.player.left",
        "chat.type.advancement.task",
        "chat.type.advancement.challenge",
        "chat.type.advancement.goal",
        "chat.type.team.sent",
        "chat.type.team.text",
        "commands.message.display.outgoing",
        "commands.message.display.incoming",
        "chat.type.announcement",
        "chat.type.emote"
    );

    {
        if (IS_LOADED) {
            ServerMessages.LOGGER.info("StyledChat has been detected, disabling duplicate messages.");
            ServerMessages.LOGGER.info("Use StyledChat for chat related messages instead.");
        }
    }

    public static boolean isDuplicate(TranslatableContents translatable) {
        return IS_LOADED && DISABLED_TRANSLATIONS.contains(translatable.getKey());
    }
}
