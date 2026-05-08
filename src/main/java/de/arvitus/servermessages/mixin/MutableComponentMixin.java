package de.arvitus.servermessages.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.arvitus.servermessages.ServerMessages;
import de.arvitus.servermessages.interfaces.IComponent;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MutableComponent.class)
public class MutableComponentMixin implements IComponent {
    @Unique
    private static boolean parsing = false;
    @Unique
    private ComponentContents original;

    @WrapMethod(
        method = "create"
    )
    private static MutableComponent parseTranslatable(
        ComponentContents contents,
        Operation<MutableComponent> original
    ) {
        var component = original.call(contents);

        ServerPlaceholderContext context;
        if (
            contents instanceof TranslatableContents translatable
            && !parsing
            && translatable.servermessages$canParse()
            && (context = ServerMessages.getContext()) != null
        ) {
            parsing = true;
            var newComponent = translatable.servermessages$parse(context);
            parsing = false;
            newComponent.servermessages$setOriginal(translatable);
            return newComponent;
        }

        return component;
    }

    @Override
    public @Nullable ComponentContents servermessages$getOriginal() {
        return original;
    }

    @Override
    public void servermessages$setOriginal(@Nullable ComponentContents original) {
        this.original = original;
    }
}
