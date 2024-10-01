package de.example.servermessages;

import eu.pb4.placeholders.api.PlaceholderContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContextStore {
    private final Map<String, PlaceholderContext> data = new HashMap<>();

    @Nullable
    public PlaceholderContext pop(String key) {
        while (!data.containsKey(key)) {
            int index = key.lastIndexOf('.');
            if (index > 0) key = key.substring(0, index);
            else break;
        }
        return data.remove(key);
    }

    public PlaceholderContext put(String key, PlaceholderContext value) {
        return data.put(key, value);
    }

    public String toString() {
        return data.toString();
    }
}
