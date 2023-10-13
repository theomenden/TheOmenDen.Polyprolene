package theomenden.polyprolene.mixin.keys;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("KEY_CATEGORIES")
    static Set<String> getKeyCategories() {
        throw new AssertionError();
    }

    @Accessor
    InputUtil.Key getBoundKey();
}
