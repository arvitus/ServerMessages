package de.arvitus.servermessages.mixin;

import de.arvitus.servermessages.interfaces.IComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Component.class)
public interface ComponentMixin extends IComponent {}
