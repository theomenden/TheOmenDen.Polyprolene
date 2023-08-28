package theomenden.polyprolene.mixin.screens;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import theomenden.polyprolene.utils.ConfigurationUtils;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        if (ConfigurationUtils.isDirectoryReadyToBeWritten()) {
            var favorites = ConfigurationUtils.getFavoritesPath();
            var history = ConfigurationUtils.getHistoryPath();

            ConfigurationUtils.createFile(favorites);
            ConfigurationUtils.createFile(history);
        }

    }
}
