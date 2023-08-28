package theomenden.polyprolene.mixin.keys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.enums.KeyBindingConflicts;
import theomenden.polyprolene.enums.ModifierKey;
import theomenden.polyprolene.enums.MouseButtons;
import theomenden.polyprolene.enums.PrefixTextVariations;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;
import theomenden.polyprolene.interfaces.IKeyConflictDeterminator;
import theomenden.polyprolene.managers.KeyBindingsManager;
import theomenden.polyprolene.models.KeyboardBindingsMap;
import theomenden.polyprolene.models.keyinfo.ModifierKeys;

import java.util.Map;

@Mixin(value = KeyBinding.class, priority = -5000)
public abstract class KeyBindingMixin implements IKeyBindingHandler {
    @Unique
    private static final KeyboardBindingsMap KEYBOARD_BINDINGS_MAP = new KeyboardBindingsMap();
    @Shadow
    @Final
    private static Map<String, KeyBinding> KEYS_BY_ID;
    @Shadow
    private InputUtil.Key boundKey;
    @Shadow
    private int timesPressed;
    @Shadow @Final private InputUtil.Key defaultKey;
    @Unique
    private ModifierKey defaultModifierKey;
    @Unique
    private ModifierKey modifierKey;

    @Unique
    private final ModifierKeys modifierKeys = new ModifierKeys();

    @Unique
    private MouseButtons mouseButton;

    @Unique
    private MouseButtons defaultMouseButton;

    @Unique
    private IKeyConflictDeterminator keyConflictDeterminator;

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        if (pressed
                && KeyBindingsManager.shouldHandleConflictingKeyBinding(key)) {
            ci.cancel();

            if (PolyproleneClient.configuration.nonConflictingKeys) {
                KeyboardBindingsMap.addBinding(key, new KeyBinding(key.getTranslationKey(), key.getCode(), key
                        .getCategory()
                        .name()));
            }
        }
    }

    @Inject(method = "updateKeysByCode", at = @At(value = "TAIL"))
    private static void updateByCodeToMultiMapping(CallbackInfo ci) {
        KeyBindingsManager.clearMappingFixes();
        KEYS_BY_ID
                .forEach((key, value) -> KeyBindingsManager
                        .(((KeyBindAccessor) value).getBoundKey(), value));
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        if (KeyBindingsManager.isConflictingKeyBind(key)) {
            KeyBindingsManager.handleKeyPressed(key);
            ci.cancel();
            KeyBindingsManager.openConflictingKeyBindingsScreen(key);
        }
    }

    @Inject(
            method = "getBoundKeyLocalizedText",
            at = @At("TAIL"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void injectOnLocalizedName(CallbackInfoReturnable<String> cir) {
        Text name = boundKey.getLocalizedText();
        Text fullKeyName;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        PrefixTextVariations variation = PrefixTextVariations.WIDEST;

        do {
            fullKeyName = name;
            for(ModifierKey mk : ModifierKey.MODIFIER_KEY_VALUES) {
                if(mk == ModifierKey.NONE) {
                    continue;
                }

                if(modifierKeys.get(mk)) {
                    fullKeyName = mk.getLocalizedName(defaultKey, () -> Text.of(""));
                    
                }
            }
        }while((variation = variation.getSmaller()) != null
        && textRenderer.getWidth(fullKeyName.toString()) > 70);

        cir.setReturnValue(fullKeyName.toString());
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
    public void addKeyToMapInjection(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        KeyboardBindingsMap.addBinding(this.boundKey, (KeyBinding) (Object) this);
    }

    @Inject(method = "isDefault", at = @At("RETURN"), cancellable = true)
    private void isADefaultModifier(CallbackInfoReturnable<Boolean> cir) {
        var modifierIsDefault = boundKey.equals(defaultModifierKey)
                && getModifier() == getDefaultModifier();

        cir.setReturnValue(modifierIsDefault);
    }

    @Inject(method = "isDefault", at = @At("RETURN"), cancellable = true)
    private void isADefaultMouseButton(CallbackInfoReturnable<Boolean> cir) {
        var mouseButtonIsDefault = boundKey.equals(defaultMouseButton)
                && getMouseButton() == getDefaultMouseButton();
        cir.setReturnValue(mouseButtonIsDefault);
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
    private void modifierKeyCheckEquals(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
        final IKeyBindingHandler extendedBinding = (IKeyBindingHandler) other;

        if (isMatchedByConflictContext(extendedBinding)) {
            return;
        }

        ModifierKey modifier = getModifier();
        ModifierKey otherModifier = extendedBinding.getModifier();

        if (modifier.isMatchedBy(extendedBinding.getKey())
                || otherModifier.isMatchedBy(boundKey)) {
            cir.setReturnValue(true);
            return;
        }

        if (boundKey.equals(extendedBinding.getKey())) {
            cir.setReturnValue(modifier == otherModifier
                    || (getConflictContext().isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && hasNoModifierKey(modifierKey) || hasNoModifierKey(otherModifier)));
        }

    }

    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void mouseButtonCheckEquals(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
        final IKeyBindingHandler extendedBinding = (IKeyBindingHandler) other;

        if (isMatchedByConflictContext(extendedBinding)) {
            return;
        }

        MouseButtons mb = getMouseButton();
        MouseButtons otherMouseButton = extendedBinding.getMouseButton();

        if (mb.isMatchedBy(extendedBinding.getKey())
                || otherMouseButton.isMatchedBy(boundKey)) {
            cir.setReturnValue(true);
            return;
        }

        if (boundKey.equals(extendedBinding.getKey())) {
            cir.setReturnValue(mb == otherMouseButton
                    || (getConflictContext().isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && hasNoMouseButton(mouseButton) || hasNoMouseButton(otherMouseButton)));
        }

    }

    @Inject(method = "getBoundKeyTranslationKey", at = @At("TAIL"), cancellable = true)
    public void getLocalizedKeyName(CallbackInfoReturnable<Text> cir) {
        Text key = boundKey.getLocalizedText();
        Text fullKeyName;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        PrefixTextVariations variation = PrefixTextVariations.WIDEST;
        do {
            fullKeyName = key;

            for (ModifierKey km : ModifierKey.MODIFIER_KEY_VALUES) {
                if (km == ModifierKey.NONE) {
                    continue;
                }

                if (ModifierKey.isModifierForKeyCode(boundKey)) {

                }
            }


        }
        while ((variation = variation.getSmaller()) != null
                && textRenderer.getWidth(fullKeyName) > 70);

        cir.setReturnValue(fullKeyName);
    }

    @Override
    public InputUtil.Key getKey() {
        return boundKey;
    }

    @Override
    public IKeyConflictDeterminator getConflictContext() {
        return keyConflictDeterminator == null ? KeyBindingConflicts.UNIVERSAL : keyConflictDeterminator;
    }

    @Override
    public ModifierKey getDefaultModifier() {
        return defaultModifierKey == null ? ModifierKey.NONE : defaultModifierKey;
    }

    @Override
    public ModifierKey getModifier() {
        return modifierKey == null ? ModifierKey.NONE : modifierKey;
    }

    @Override
    public MouseButtons getDefaultMouseButton() {
        return defaultMouseButton == null ? MouseButtons.NONE : defaultMouseButton;
    }

    @Override
    public MouseButtons getMouseButton() {
        return mouseButton == null ? MouseButtons.NONE : mouseButton;
    }

    @Override
    public void setKeyDeterminatorContext(IKeyConflictDeterminator determinatorContext) {
        this.keyConflictDeterminator = determinatorContext;
    }

    @Override
    public void setModifierWithCodeForKey(ModifierKey keyModifier, InputUtil.Key key) {
        this.boundKey = key;

        if (keyModifier.isMatchedBy(key)) {
            keyModifier = ModifierKey.NONE;
        }

        KeyboardBindingsMap.removeBinding((KeyBinding) (Object) (this));
        this.modifierKey = keyModifier;
        KeyboardBindingsMap.addBinding(key, (KeyBinding) (Object) this);

    }

    @Override
    public void onPress() {
        ++timesPressed;
    }


    @Unique
    private boolean isMatchedByConflictContext(IKeyBindingHandler extendedBinding) {
        return !getConflictContext().isAConflictWith(extendedBinding.getConflictContext())
                && !extendedBinding
                .getConflictContext()
                .isAConflictWith(getConflictContext());
    }

    @Unique
    private boolean hasNoModifierKey(ModifierKey modifierKey) {
        return modifierKey == ModifierKey.NONE;
    }

    @Unique
    private boolean hasNoMouseButton(MouseButtons mouseButton) {
        return mouseButton == MouseButtons.NONE;
    }
}