package theomenden.polyprolene.mixin.fabricapi;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = KeyBindingRegistryImpl.class, remap = false)
public interface KeyBindingRegistryImplAccessorMixin {
    @Accessor("MODDED_KEY_BINDINGS")
    static @NotNull List<KeyBinding> getModdedKeyBindings() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("MODDED_KEY_BINDINGS")
    static void setModdedKeyBindings(@NotNull List<KeyBinding> keyBindings) {
        throw new AssertionError();
    }
}
