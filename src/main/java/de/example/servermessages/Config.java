package de.example.servermessages;

import com.google.common.collect.ImmutableMap;
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

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import static de.example.servermessages.ServerMessages.*;

public class Config {
    public static final Path PATH = CONFIG_DIR.resolve("config.json");
    public static final ParserContext.Key<Function<String, Text>> DYN_KEY = DynamicTextNode.key(MOD_ID);
    public static final NodeParser PARSER = NodeParser.builder()
        .quickText()
        .simplifiedTextFormat()
        .legacyAll()
        .globalPlaceholders(TagLikeParser.PLACEHOLDER_USER)
        .placeholders(TagLikeParser.Format.of('%', 's'), DYN_KEY)
        .staticPreParsing()
        .build();
    public static final Codec<Map<String, WrappedLangText>> CODEC = Codec.unboundedMap(
        Codec.STRING,
        Codec.STRING.xmap(x -> WrappedLangText.from(PARSER, x), WrappedLangText::input)
    );
    private static final ImmutableMap<String, WrappedLangText> DEFAULT_DATA = ImmutableMap.of(
        "multiplayer.disconnect.not_whitelisted",
        WrappedLangText.from(PARSER, "<lang multiplayer.disconnect.not_whitelisted>"),
        "multiplayer.disconnect.server_shutdown",
        WrappedLangText.from(PARSER, "<lang multiplayer.disconnect.server_shutdown>")
    );
    private static ImmutableMap<String, WrappedLangText> data = DEFAULT_DATA;
    private static boolean ready = false;

    private Config() {}

    public static void load() {
        Config.ready = false;
        Map<String, WrappedLangText> data = null;
        try (Reader reader = new FileReader(PATH.toFile())) {
            DataResult<Map<String, WrappedLangText>> result = CODEC.parse(
                JsonOps.INSTANCE,
                JsonParser.parseReader(reader)
            );
            data = result.getOrThrow();
        } catch (FileNotFoundException e) {
            Config.createDefaultFile();
            data = DEFAULT_DATA;
        } catch (Exception e) {
            LOGGER.warn("Error during config load, using previous config instead", e);
        }

        if (data != null) Config.data = ImmutableMap.copyOf(data);
        Config.ready = true;
    }

    private static void createDefaultFile() {
        CONFIG_DIR.toFile().mkdir();
        try (Writer writer = new FileWriter(PATH.toFile())) {
            DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT_DATA);
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(result.getOrThrow(), writer);
        } catch (Exception e) {
            LOGGER.warn("Error during creation of default config", e);
        }
    }

    public static boolean isReady() {
        return Config.ready;
    }

    public static WrappedLangText get(String key) {
        return Config.getData().get(key);
    }

    public static boolean contains(String key) {
        return Config.getData().containsKey(key);
    }

    public static ImmutableMap<String, WrappedLangText> getData() {
        if (Config.data == null) Config.load();
        return Config.data;
    }
}
