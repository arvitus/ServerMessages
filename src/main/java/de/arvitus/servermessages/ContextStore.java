package de.arvitus.servermessages;

import eu.pb4.placeholders.api.ServerPlaceholderContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContextStore {
    private final Map<String, ServerPlaceholderContext> data = new HashMap<>();

    @Nullable
    public ServerPlaceholderContext pop(String key) {
        while (!data.containsKey(key)) {
            int index = key.lastIndexOf('.');
            if (index > 0) key = key.substring(0, index);
            else break;
        }
        return data.remove(key);
    }

    public ServerPlaceholderContext put(String key, ServerPlaceholderContext value) {
        return data.put(key, value);
    }

    public String toString() {
        return data.toString();
    }
}
