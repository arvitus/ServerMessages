package de.arvitus.servermessages;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderAPITags {
    public static String RAW_KEY = "#raw";

    public static void register() {
        // Copied and extended from eu.pb4.placeholders.impl.textparser.BuiltinTags
        TagRegistry.registerDefault(TextTag.self(
                "rawlang",
                List.of("rawtranslate", "rlang", "rtranslate"),
                "special",
                false,
                (nodes, data, parser) -> {
                    if (!data.isEmpty()) {
                        var key = data.getNext("key");
                        var fallback = data.get("fallback");

                        List<TextNode> textList = new ArrayList<>();
                        int i = 0;
                        while (true) {
                            var part = data.getNext("" + (i++));
                            if (part == null) {
                                break;
                            }
                            textList.add(parser.parseNode(part));
                        }

                        textList.add(parser.parseNode(RAW_KEY));

                        return TranslatedNode.ofFallback(key, fallback, (Object[]) textList.toArray(TextNode[]::new));
                    }
                    return TextNode.empty();
                }
            )
        );
    }
}
