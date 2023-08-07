package theomenden.polyprolene.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;

@Mixin(StickyKeyBinding.class)
public abstract class StickyKeyMixin extends KeyBinding {
    public StickyKeyMixin(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Redirect(method = "setPressed",
    at = @At(value="INVOKE",
    target = "Lnet/minecraft/client/option/KeyBinding;setPressed(Z)V",
    ordinal = 0))
    private void proxySetPressed(KeyBinding instance, boolean pressed) {
        if(!((IKeyBindingHandler)this).isActiveModifierWithContext()) {
            return;
        }
        super.setPressed(!isPressed());
    }
}
