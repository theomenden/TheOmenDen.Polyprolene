package theomenden.polyprolene.mixin.keys;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;
import theomenden.polyprolene.managers.KeyBindingsManager;

@Mixin(value = KeyBinding.class, priority = -5000)
public abstract class KeyBindingMixin implements IKeyBindingHandler {
    @Shadow
    private InputUtil.Key boundKey;

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) throws Exception {
        if (pressed) {
            boolean conflicting = KeyBindingsManager.shouldHandleConflictingKeyBinding(key);
            if (conflicting) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        if (KeyBindingsManager.isConflictingKeyBind(key)) {
            ci.cancel();
            KeyBindingsManager.openConflictingKeyBindingsScreen(key);
        }
    }

    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        if (KeyBindingsManager.isConflictingKeyBind(this.boundKey)) {
            ci.cancel();
        }
    }
}