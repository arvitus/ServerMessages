package de.arvitus.servermessages.mixin;

import de.arvitus.servermessages.Config;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.CommonColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.arvitus.servermessages.ServerMessages.*;

@Mixin(MutableComponent.class)
public abstract class MutableComponentMixin {
    @Unique
    private static boolean parsing = false;

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void replaceCustomTranslations(
        ComponentContents content,
        CallbackInfoReturnable<MutableComponent> cir
    ) {
        if (
            !ServerMessages.isEnabled() ||
            parsing ||
            !(content instanceof TranslatableContents translatable) ||
            !Config.contains(translatable.getKey())
        ) return;

        parsing = true;
        TextNode node = Objects.requireNonNull(Config.get(translatable.getKey())).textNode();
        parsing = false;

        Map<String, Component> argPlaceholders = new HashMap<>();
        if (translatable.getArgs().length > 0) {
            for (Object arg : Arrays.stream(translatable.getArgs()).toList()) {
                Component text;
                if (arg instanceof Component t) text = t;
                else text = Component.literal(String.valueOf(arg));
                argPlaceholders.put((argPlaceholders.size() + 1) + "$", text);
            }
        }

        ServerPlaceholderContext storedContext = CONTEXT_STORE.pop(translatable.getKey());
        if (storedContext == null && SERVER != null) storedContext = ServerPlaceholderContext.of(SERVER);

        ParserContext context = ParserContext.of(Config.DYN_KEY, argPlaceholders::get);
        if (storedContext != null) context.with(ServerPlaceholderContext.SERVER_KEY, storedContext);

        parsing = true;
        MutableComponent text;
        try {
            text = node.toComponent(context).copy();
        } catch (Exception e) {
            LOGGER.error("An error has occurred during node parsing:", e);
            text = Component.literal("An error has occurred. See console for details.").copy().withColor(
                CommonColors.RED);
        }
        parsing = false;

        cir.setReturnValue(text);
    }
}
