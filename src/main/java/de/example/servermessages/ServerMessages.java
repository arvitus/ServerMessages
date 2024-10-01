package de.example.servermessages;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ServerMessages implements ModInitializer {
    public static final String MOD_ID = "servermessages";
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ContextStore CONTEXT_STORE = new ContextStore();
    @Nullable
    public static MinecraftServer SERVER;


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

        Config.load();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> SERVER = null);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> Config.load());
    }
}
