package theomenden.polyprolene.models;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import theomenden.polyprolene.mixin.keys.KeyBindAccessor;

import java.util.Arrays;
import java.util.function.Consumer;

import static theomenden.polyprolene.models.AutoCompleteResult.addToSuggestionHistory;

public class KeyBindSuggestion {
    public Text name;
    public Text category;
    public KeyBinding initialBinding;
    public boolean isAFavorite = false;
    private final String searchText;

    public KeyBindSuggestion(KeyBinding binding) {
        initialBinding = binding;
        name = generateTranslatableText(binding.getTranslationKey());
        category = generateTranslatableText(binding.getCategory());
        searchText = (name.getString() + " " + category.getString()).toLowerCase();
    }

    protected Text generateTranslatableText(String key) {
        return Text.translatable(key);
    }

    protected KeyBinding getFullscreenKey(GameOptions options) {
        return options.fullscreenKey;
    }

    protected void saveFullscreenState(GameOptions options, boolean isFullscreen) {
        options
                .getFullscreen()
                .setValue(isFullscreen);
    }

    protected KeyBinding getScreenshotKey(GameOptions options) {
        return options.screenshotKey;
    }

    protected void takeScreenshot(MinecraftClient minecraftClient, Consumer<Text> messageConsumer) {
        ScreenshotRecorder.saveScreenshot(minecraftClient.runDirectory, minecraftClient.getFramebuffer(), messageConsumer);
    }

    public boolean matches(String[] searchTerms) {
        return Arrays
                .stream(searchTerms)
                .anyMatch(term -> searchText.contains(term));
    }

    public void execute() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        GameOptions gameOptions = minecraftClient.options;

        addToSuggestionHistory(getId());

        if (initialBinding.equals(getFullscreenKey(gameOptions))) {
            minecraftClient
                    .getWindow()
                    .toggleFullscreen();
            saveFullscreenState(minecraftClient.options, minecraftClient
                    .getWindow()
                    .isFullscreen());
            minecraftClient.options.write();

            return;
        }

        if (initialBinding.equals(getScreenshotKey(gameOptions))) {
            takeScreenshot(minecraftClient, consumer -> minecraftClient.execute(() -> minecraftClient.inGameHud
                    .getChatHud()
                    .addMessage(consumer)));

            return;
        }

        var currentPressedTimes = ((KeyBindAccessor) initialBinding).getTimesPressed();
        ((KeyBindAccessor) initialBinding).setTimesPressed(currentPressedTimes + 1);

        ((KeyBindAccessor) initialBinding).setPressed(true);
        ((KeyBindAccessor) initialBinding).setPressed(false);

        if (FabricLoader
                .getInstance()
                .isModLoaded("amecsapi")) {

        }
    }

    public String getId() {
        return initialBinding.getTranslationKey();
    }
}
