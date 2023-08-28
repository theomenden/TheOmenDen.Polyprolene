package theomenden.polyprolene.mixin.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlOptionsScreenMixin extends GameOptionsScreen {

    public ControlOptionsScreenMixin(Screen screen, GameOptions options, Text title) {
        super(screen, options, title);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        TexturedButtonWidget screenToggler = new TexturedButtonWidget(
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
                button -> {
                    client.setScreen(new PolyproleneKeyboardScreen(this.parent));
                }
        );
        this.addDrawableChild(screenToggler);

    }

    @Unique
    protected void onClickHandler(ButtonWidget btn) {
        assert client != null;
        PolyproleneKeyboardScreen screen = new PolyproleneKeyboardScreen(this);

        client.setScreen(screen);
    }

}
