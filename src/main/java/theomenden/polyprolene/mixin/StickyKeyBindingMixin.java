package theomenden.polyprolene.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import theomenden.polyprolene.manager.KeyBindingsManager;

@Mixin(StickyKeyBinding.class)
public abstract class StickyKeyBindingMixin extends KeyBinding {
    public StickyKeyBindingMixin(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Redirect(method = "setPressed",
    at = @At(value="INVOKE",
    target="Lnet/minecraft/client/option/KeyBinding;setPressed(Z)V",
    ordinal = 0))
    private void proxySetPressed(KeyBinding instance, boolean pressed) {
        if(!KeyBindingsManager.isConflictingKeyBind(instance.getDefaultKey())) {
            return;
        }
        super.setPressed(!isPressed());
    }
}
