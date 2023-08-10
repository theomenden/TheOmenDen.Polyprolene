package theomenden.polyprolene.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(KeyBinding.class)
public interface KeyBindAccessorMixin {
    @Accessor
    static Set<String> getKEY_CATEGORIES() {
        throw new AssertionError();
    }

    @Accessor InputUtil.Key getBoundKey();
    @Accessor int  getTimesPressed();
    @Accessor void setTimesPressed(int timesPressed);
    @Accessor void setPressed(boolean wasPressed);
}
