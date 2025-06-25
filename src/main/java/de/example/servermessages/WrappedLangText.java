package de.example.servermessages;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;

public record WrappedLangText(String input, TextNode textNode) {
    public static WrappedLangText from(NodeParser parser, String input) {
        int index = 1;
        while (input.contains("%s")) {
            input = input.replaceFirst("%s", "%" + index + "\\$s");
            index++;
        }

        var node = parser.parseNode(input);

        return new WrappedLangText(input, node);
    }
}
