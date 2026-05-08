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
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.arvitus.servermessages.PlaceholderAPITags.RAW_KEY;
import static de.arvitus.servermessages.ServerMessages.LOGGER;

@Mixin(TranslatableContents.class)
public class TranslatableContentsMixin implements IParseable {
    @Unique
    private boolean isRaw = false;
    @Shadow
    @Final
    private String key;
    @Shadow
    @Final
    @Mutable
    private Object[] args;

    @ModifyVariable(
        method = "<init>",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/network/chat/contents/TranslatableContents;args:[Ljava/lang/Object;",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.BEFORE
        ),
        argsOnly = true,
        name = "args"
    )
    private Object[] setRawMode(Object[] args) {
        if (args.length > 0) {
            var lastArg = args[args.length - 1];
            var value = switch (lastArg) {
                case String s -> s;
                case Component c when c.getContents() instanceof PlainTextContents t -> t.text();
                default -> null;
            };
            if (Objects.equals(value, RAW_KEY)) {
                isRaw = true;
                args = Arrays.copyOf(args, args.length - 1);
            }
        }
        return args;
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
        return !isRaw && Config.contains(key);
    }
}
