package theomenden.polyprolene.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theomenden.polyprolene.manager.KeyBindingsManager;

import java.util.Map;

@Mixin(value = KeyBinding.class, priority = -5000)
public class KeyBindingMixin {
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) throws Exception {
        if (pressed
                && KeyBindingsManager.shouldHandleConflictingKeyBinding(key)) {
            ci.cancel();
        }
    }

    @Inject(method="updateKeysByCode", at = @At(value="TAIL"))
    private static void updateByCodeToMultiMapping(CallbackInfo ci) {
        KeyBindingsManager.clearMappingFixes();
        KEYS_BY_ID.entrySet()
                .stream()
                .forEach(entry -> {
                    KeyBindingsManager
                            .addKeyToFixingMap(((KeyBindAccessorMixin)entry.getValue()).getBoundKey(), entry.getValue());
                });
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        if (KeyBindingsManager.isConflictingKeyBind(key)) {
            KeyBindingsManager.handleKeyPressed(key);
            ci.cancel();
            KeyBindingsManager.openConflictingKeyBindingsScreen(key);
        }
    }

    @Inject(method="<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at=@At("TAIL"))
    private void addToMultiMapping(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        KeyBindingsManager.addKeyToFixingMap(boundKey, (KeyBinding)(Object)this);
    }
    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        if (KeyBindingsManager.isConflictingKeyBind(this.boundKey)) {
            ci.cancel();
        }
    }
}