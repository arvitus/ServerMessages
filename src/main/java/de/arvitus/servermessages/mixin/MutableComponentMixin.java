package de.arvitus.servermessages.mixin;

import de.arvitus.servermessages.Config;
import de.arvitus.servermessages.ServerMessages;
import eu.pb4.placeholders.api.ParserContext;
import de.arvitus.servermessages.interfaces.IParseable;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MutableComponent.class)
public class MutableComponentMixin implements IParseable {
    @Unique
    private static final List<String> parsing = new ArrayList<>();
    @Shadow
    @Final
    private ComponentContents contents;
    @Shadow
    private Style style;
    @Unique
    private MutableComponent original;

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

    @Override
    public @Nullable ServerPlaceholderContext servermessages$getContext() {
        return getParsingTarget().servermessages$getContext();
    }

    @Override
    public void servermessages$setContext(ServerPlaceholderContext context) {
        getParsingTarget().servermessages$setContext(context);
    }

    @Override
    public MutableComponent servermessages$parse(@Nullable ServerPlaceholderContext context) {
        if (!servermessages$canParse()) return (MutableComponent) (Object) this;
        var component = getParsingTarget().servermessages$parse(context);
        component.servermessages$setOriginal(this.original != null ? this.original : (MutableComponent) (Object) this);
        return component.withStyle(component.getStyle().applyTo(style));
    }

    @Override
    public boolean servermessages$canParse() {
        return getParsingTarget().servermessages$canParse();
    }

    @Unique
    private IParseable getParsingTarget() {
        if (this.original != null) return this.original;
        return contents;
    }

    @Override
    public @Nullable MutableComponent servermessages$getOriginal() {
        return original;
    }

    @Override
    public void servermessages$setOriginal(@Nullable MutableComponent original) {
        this.original = original;
    }
}
