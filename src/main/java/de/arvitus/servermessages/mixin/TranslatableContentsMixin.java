package de.arvitus.servermessages.mixin;

import de.arvitus.servermessages.Config;
import de.arvitus.servermessages.interfaces.IParseable;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.arvitus.servermessages.ServerMessages.LOGGER;

@Mixin(TranslatableContents.class)
public class TranslatableContentsMixin implements IParseable {
    @Shadow
    @Final
    private String key;
    @Shadow
    @Final
    private Object[] args;
    @Unique
    private @Nullable ServerPlaceholderContext context;

    @Override
    public @Nullable ServerPlaceholderContext servermessages$getContext() {
        return context;
    }

    @Override
    public void servermessages$setContext(ServerPlaceholderContext context) {
        this.context = context;
    }

    @Override
    public MutableComponent servermessages$parse(@Nullable ServerPlaceholderContext context) {
        if (context == null || !Config.contains(key))
            return MutableComponent.create((TranslatableContents) (Object) this);

        TextNode node = Objects.requireNonNull(Config.get(key)).textNode();

        Map<String, Component> argPlaceholders = new HashMap<>();
        for (var arg : args) {
            var text = arg instanceof Component c ? c : Component.literal(String.valueOf(arg));
            argPlaceholders.put((argPlaceholders.size() + 1) + "$", text);
        }

        var parserContext = ParserContext.of(Config.DYN_KEY, argPlaceholders::get);
        parserContext.with(ServerPlaceholderContext.SERVER_KEY, context);

        MutableComponent component;
        try {
            component = (MutableComponent) node.toComponent(parserContext);
        } catch (Exception e) {
            LOGGER.error("An error has occurred during node parsing:", e);
            component = Component
                .literal("An error has occurred. See console for details.")
                .withColor(CommonColors.RED);
        }

        return component;
    }

    @Override
    public boolean servermessages$canParse() {
        if (!Config.contains(key)) return false;

        if (args.length > 0) {
            var lastArg = args[args.length - 1];
            var value = switch (lastArg) {
                case String s -> s;
                case Component c when c.getContents() instanceof PlainTextContents t -> t.text();
                default -> null;
            };
            return !Objects.equals(value, "#raw");
        }

        return true;
    }
}
