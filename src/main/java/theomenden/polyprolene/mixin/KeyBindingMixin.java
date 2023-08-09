package theomenden.polyprolene.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.enums.KeyBindingConflicts;
import theomenden.polyprolene.enums.ModifierKeys;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;
import theomenden.polyprolene.interfaces.IKeyConflictDeterminator;
import theomenden.polyprolene.manager.KeyBindingsManager;
import theomenden.polyprolene.models.KeyboardBindingsMap;

import java.util.Map;

@Mixin(value = KeyBinding.class, priority = -5000)
public abstract class KeyBindingMixin implements IKeyBindingHandler {
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow
    @Final
    private static Map<String, KeyBinding> KEYS_BY_ID;

    @Shadow private int timesPressed;
    private static final KeyboardBindingsMap KEYBOARD_BINDINGS_MAP = new KeyboardBindingsMap();

    private ModifierKeys defaultModifierKey;
    private ModifierKeys modifierKey;

    private IKeyConflictDeterminator keyConflictDeterminator;

    @Override
    public InputUtil.Key getKey() {
        return boundKey;
    }

    @Override
    public IKeyConflictDeterminator getConflictContext() {
        return keyConflictDeterminator == null ? KeyBindingConflicts.UNIVERSAL : keyConflictDeterminator;
    }

    @Override
    public ModifierKeys getDefaultModifier() {
        return defaultModifierKey == null ? ModifierKeys.NONE : defaultModifierKey;
    }

    @Override
    public ModifierKeys getModifier() {0
        return modifierKey == null ? ModifierKeys.NONE : modifierKey;
    }

    @Override
    public void setKeyDeterminatorContext(IKeyConflictDeterminator determinatorContext) {
        this.keyConflictDeterminator = determinatorContext;
    }
    @Override
    public void setModifierWithCodeForKey(ModifierKeys keyModifier, InputUtil.Key key) {
        this.boundKey = key;

        if(keyModifier.isMatchedBy(key)) {
            keyModifier = ModifierKeys.NONE;
        }

        KEYBOARD_BINDINGS_MAP.removeBinding((KeyBinding) (Object)(this));
        this.modifierKey = keyModifier;
        KEYBOARD_BINDINGS_MAP.addBinding(key, (KeyBinding) (Object)this);

    }

    @Override
    public void onPress() {
        ++timesPressed;
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
    public void addKeyToMapInjection(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        KEYBOARD_BINDINGS_MAP.addBinding(this.boundKey, (KeyBinding) (Object)this);
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        if (pressed
                && KeyBindingsManager.shouldHandleConflictingKeyBinding(key)) {
            ci.cancel();

            if(PolyproleneClient.configuration.nonConflictingKeys) {
                KEYBOARD_BINDINGS_MAP.
            }
        }
    }

    @Inject(method = "updateKeysByCode", at = @At(value = "TAIL"))
    private static void updateByCodeToMultiMapping(CallbackInfo ci) {
        KeyBindingsManager.clearMappingFixes();
        KEYS_BY_ID
                .entrySet()
                .forEach(entry -> {
                    KeyBindingsManager
                            .addKeyToFixingMap(((KeyBindAccessorMixin) entry.getValue()).getBoundKey(), entry.getValue());
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

    @Inject(method="isDefault", at=@At("RETURN"), cancellable = true)
    private void isADefaultModifier(CallbackInfoReturnable<Boolean> cir) {
        var modifierIsDefault = boundKey.equals(defaultModifierKey)
                && getModifier() == getDefaultModifier();

        cir.setReturnValue(modifierIsDefault);
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("TAIL"))
    private void addToMultiMapping(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        KeyBindingsManager.addKeyToFixingMap(boundKey, (KeyBinding) (Object) this);
    }

    @Inject(method = "setPressed", at = @At("HEAD"), cancellable = true)
    private void setPressed(boolean pressed, CallbackInfo ci) {
        if (KeyBindingsManager.isConflictingKeyBind(this.boundKey)) {
            ci.cancel();
        }
    }

    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void extendedEquals(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
        final IKeyBindingHandler extendedBinding = (IKeyBindingHandler) other;

        if(isMatchedByConflictContext(extendedBinding)) {
            return;
        }

        ModifierKeys modifier = getModifier();
        ModifierKeys otherModifier = extendedBinding.getModifier();

        if(modifier.isMatchedBy(extendedBinding.getKey())
        || otherModifier.isMatchedBy(getKey())) {
            cir.setReturnValue(true);
            return;
        }

        if(getKey().equals(extendedBinding.getKey())) {
            cir.setReturnValue(modifier == otherModifier
            || (getConflictContext().isAConflictWith(KeyBindingConflicts.IN_GAME)
            && hasNoModifierKey(modifierKey) || hasNoModifierKey(otherModifier)));
        }

    }

    @Inject(method = "getBoundKeyTranslationKey", at = @At("RETURN"), cancellable = true)
    private void combineLocalizedText(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(getModifier().getLocalizedName(boundKey, () -> this.boundKey.getLocalizedText()));
    }

    private boolean isMatchedByConflictContext(IKeyBindingHandler extendedBinding) {
        return !getConflictContext().isAConflictWith(extendedBinding.getConflictContext())
                && !extendedBinding.getConflictContext().isAConflictWith(getConflictContext());
    }

    private boolean hasNoModifierKey(ModifierKeys modifierKey) {
        return modifierKey == ModifierKeys.NONE;
    }
}