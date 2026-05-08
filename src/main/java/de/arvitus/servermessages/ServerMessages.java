package de.arvitus.servermessages;

import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Supplier;

public class ServerMessages implements ModInitializer {
    public static final String MOD_ID = "servermessages";
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Nullable
    public static MinecraftServer SERVER;

    private static ServerPlaceholderContext context;

    public static @Nullable ServerPlaceholderContext getContext() {
        if (context != null) return context;
        if (SERVER != null) return ServerPlaceholderContext.of(SERVER);
        return null;
    }

    public static <T> T withContext(ServerPlaceholderContext context, Supplier<T> supplier) {
        ServerMessages.context = context;
        var res = supplier.get();
        ServerMessages.context = null;
        return res;
    }

    public static MutableComponent parseWithContext(ServerPlaceholderContext context, ComponentContents contents) {
        return withContext(context, () -> MutableComponent.create(contents));
    }

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            ModMetadata meta = modContainer.getMetadata();
            LOGGER.info(
                "Loaded {} v{} by {}",
                meta.getName(),
                meta.getVersion(),
                meta.getAuthors().stream().findFirst().map(Person::getName).orElse("unknown")
            );
        });

        PlaceholderAPITags.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER = null);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> Config.load());
    }
}
