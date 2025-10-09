package de.arvitus.servermessages;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.DynamicTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.arvitus.servermessages.ServerMessages.*;

public class Config {
    public static final Path PATH = CONFIG_DIR.resolve("config.json");
    public static final Codec<Map<String, String>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING);
    public static final ParserContext.Key<Function<String, Text>> DYN_KEY = DynamicTextNode.key(MOD_ID);
    public static final NodeParser PARSER = NodeParser.builder()
        .quickText()
        .simplifiedTextFormat()
        .legacyAll()
        .globalPlaceholders(TagLikeParser.PLACEHOLDER_USER)
        .placeholders(TagLikeParser.Format.of('%', 's'), DYN_KEY)
        .staticPreParsing()
        .build();

    private static Map<String, WrappedLangText> cache;
    private static Map<String, String> data;

    private Config() {}

    public static void load() {
        if (data == null)
            data = Map.of(
                "multiplayer.disconnect.not_whitelisted", "<lang multiplayer.disconnect.not_whitelisted>",
                "multiplayer.disconnect.server_shutdown", "<lang multiplayer.disconnect.server_shutdown>"
            );

        try (Reader reader = new FileReader(PATH.toFile())) {
            DataResult<Map<String, String>> result = CODEC.parse(
                JsonOps.INSTANCE,
                JsonParser.parseReader(reader)
            );
            data = result.getOrThrow();
        } catch (FileNotFoundException e) {
            save();
        } catch (Exception e) {
            LOGGER.warn("Error during config load, using previous value instead", e);
        }

        getCache().clear();
    }

    public static void save() {
        try (Writer writer = new FileWriter(PATH.toFile())) {
            DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, getData());
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(result.getOrThrow(), writer);
        } catch (Exception e) {
            LOGGER.warn("Error during config save", e);
        }
    }

    private static Map<String, String> getData() {
        if (data == null) load();
        return data;
    }

    private static Map<String, WrappedLangText> getCache() {
        if (cache == null) cache = new HashMap<>();
        return cache;
    }

    public static @Nullable WrappedLangText get(String key) {
        if (!getCache().containsKey(key) && getData().containsKey(key)) {
            String value = data.get(key);
            cache.put(key, WrappedLangText.from(PARSER, value));
        }
        return cache.get(key);
    }

    public static boolean contains(String key) {
        return getData().containsKey(key);
    }
}