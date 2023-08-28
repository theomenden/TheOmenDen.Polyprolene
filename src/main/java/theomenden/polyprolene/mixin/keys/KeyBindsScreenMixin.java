package theomenden.polyprolene.mixin.keys;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import theomenden.polyprolene.enums.ModifierKey;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;

import java.util.Arrays;

@Mixin(KeybindsScreen.class)
public abstract class KeyBindsScreenMixin extends GameOptionsScreen {

    @Shadow
    @Nullable
    public KeyBinding selectedKeyBinding;

    @Shadow
    public long lastKeyCodeUpdateTime;

    @Shadow
    private ControlsListWidget controlsList;

    public KeyBindsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Redirect(method = "init", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/option/KeybindsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
            ordinal = 0
    ))
    private Element proxyInit(KeybindsScreen instance, Element element) {
        return this.addDrawableChild(ButtonWidget
                .builder(Text.translatable(".resetAll"), b -> {
                    Arrays
                            .stream(this.gameOptions.allKeys)
                            .forEach(kb -> ((IKeyBindingHandler) kb).setToDefault());
                    controlsList.update();
                })
                .dimensions(this.width / 2 - 155, this.height - 29, 150, 20)
                .build());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void onHeadKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (selectedKeyBinding == null) {
            cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
            return;
        }

        final IKeyBindingHandler extendedBinding = (IKeyBindingHandler) selectedKeyBinding;

        if (keyCode == 256) {
            extendedBinding.setModifierWithCodeForKey(ModifierKey.getActiveModifier(), InputUtil.UNKNOWN_KEY);
            this.gameOptions.setKeyCode(selectedKeyBinding, InputUtil.UNKNOWN_KEY);
        } else {
            extendedBinding.setModifierWithCodeForKey(ModifierKey.getActiveModifier(), InputUtil.fromKeyCode(keyCode, scanCode));
            this.gameOptions.setKeyCode(selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
        }

        if (ModifierKey.isModifierForKeyCode(((IKeyBindingHandler) selectedKeyBinding).getKey())) {
            selectedKeyBinding = null;
        }

        this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
        KeyBinding.updateKeysByCode();
        cir.setReturnValue(true);
    }
}
