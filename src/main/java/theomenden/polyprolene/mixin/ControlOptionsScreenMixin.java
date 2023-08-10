package theomenden.polyprolene.mixin;

import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.utils.LoggerUtils;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlOptionsScreenMixin extends GameOptionsScreen {
    private ControlOptionsScreenMixin() {
        super(null, null,null);
    }

    @Inject(at = @At("TAIL"), method="init")
    private void injectAtInitTail(CallbackInfo ci) {
        LoggerUtils.getLoggerInstance().info("Polyprolene keyboard injected \u2380");
        TexturedButtonWidget screenToggler = new TexturedButtonWidget(this.width - 22, this.height - 22, 20, 20, 0, 0, 20, PolyproleneClient.SCREEN_WIDGETS, 40, 40, (btn) -> client.setScreen(new PolyproleneKeyboardScreen(this.parent)));
        this.addSelectableChild(screenToggler);
    }
}
