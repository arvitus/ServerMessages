package de.example.servermessages.mixin;

import de.example.servermessages.Config;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static de.example.servermessages.ServerMessages.*;

@Mixin(MutableText.class)
public abstract class MutableTextMixin {
    @Unique
    private static boolean parsing = false;

    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void replaceCustomTranslations(TextContent content, CallbackInfoReturnable<MutableText> cir) {
        if (
            // (SERVER != null && !SERVER.isOnThread()) || // only call on logical server
            parsing ||
            !Config.isReady() || // to avoid recursion
            !(content instanceof TranslatableTextContent translatable) ||
            !Config.contains(translatable.getKey())
        ) return;

        TextNode node = Config.get(translatable.getKey()).textNode();

        Map<String, Text> argPlaceholders = new HashMap<>();
        if (translatable.getArgs().length > 0) {
            for (Object arg : Arrays.stream(translatable.getArgs()).toList()) {
                Text text;
                if (arg instanceof Text t) text = t;
                else text = Text.of(String.valueOf(arg));
                argPlaceholders.put((argPlaceholders.size() + 1) + "$", text);
            }
        }

        PlaceholderContext storedContext = CONTEXT_STORE.pop(translatable.getKey());
        if (storedContext == null && SERVER != null) storedContext = PlaceholderContext.of(SERVER);

        ParserContext context = ParserContext.of(Config.DYN_KEY, argPlaceholders::get);
        if (storedContext != null) context.with(PlaceholderContext.KEY, storedContext);

        parsing = true;
        MutableText text;
        try {
            text = node.toText(context).copy();
        } catch (Exception e) {
            LOGGER.error("An error has occurred during node parsing:", e);
            text = Text.of("An error has occurred. See console for details.").copy().withColor(Colors.RED);
        }
        parsing = false;

        cir.setReturnValue(text);
    }
}
