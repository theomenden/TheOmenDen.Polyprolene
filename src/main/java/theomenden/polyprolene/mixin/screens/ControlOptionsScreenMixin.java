package theomenden.polyprolene.mixin.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlOptionsScreenMixin extends GameOptionsScreen {
    public ControlOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(null, null, null);
    }

    @Shadow
    protected abstract void method_19872(ButtonWidget par1);

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    private void onInit(CallbackInfo ci) {
        TexturedButtonWidget toggleScreenButton = new TexturedButtonWidget(
                this.width - 22,
                this.height - 22,
                20,
                20,
                0,
                0,
                20,
                PolyproleneClient.SCREEN_WIDGETS,
                40,
                40,
                btn -> {
                    client.setScreen(new PolyproleneKeyboardScreen(this.parent));
                }
        );
        this.addDrawableChild(toggleScreenButton);
    }
}
