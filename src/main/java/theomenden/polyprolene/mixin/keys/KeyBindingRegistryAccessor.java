package theomenden.polyprolene.mixin.keys;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = KeyBindingRegistryImpl.class, remap = false)
public interface KeyBindingRegistryAccessor {
    @Accessor("MODDED_KEY_BINDINGS")
    public static List<KeyBinding> getModdedKeyBindings() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("MODDED_KEY_BINDINGS")
    public static void setModdedKeyBindings(@NotNull List<KeyBinding> moddedKeyBindings) {
        throw new AssertionError();
    }
}
