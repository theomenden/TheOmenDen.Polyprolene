package theomenden.polyprolene.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theomenden.polyprolene.models.AutoCompleteResult;
import theomenden.polyprolene.models.keyinfo.ModifierKeys;
import theomenden.polyprolene.providers.VanillaKeyBindingSuggestionProvider;

public final class PolyproleneClient implements ClientModInitializer {

    public static final String MODID = "polyprolene";
    public static final Identifier SCREEN_WIDGETS = new Identifier(MODID, "assets/screen_toggler.png");
    public static final ModifierKeys CURRENT_MODIFIERS = new ModifierKeys();
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static PolyproleneConfig configuration;
    public static KeyBinding launchingKey;
    public static KeyBinding favoriteKey;
    public static KeyBinding wizardkey;

    public static VanillaKeyBindingSuggestionProvider createVanillaKeyBindingSuggestions() {
        return new VanillaKeyBindingSuggestionProvider();
    }

    public static void openNewPolyproleneRadialScreen(MinecraftClient client) {
        client.setScreen(new PolyproleneRadialScreen());
    }

    public static void openNewPolyproleneWizardScreen(MinecraftClient client) {
        client.setScreen(new PolyproleneKeyboardScreen(client.currentScreen));
    }

    public static void openNewPolyproleneScreen(MinecraftClient client) {
        client.setScreen(new PolyproleneScreen());
    }

    private static void registerScreensForDisplay(MinecraftClient client) {
        if (launchingKey.wasPressed()) {
            if (configuration.shouldShowRadial) {
                openNewPolyproleneRadialScreen(client);
            } else {
                openNewPolyproleneScreen(client);
            }
        }

        if (wizardkey.wasPressed()) {
            openNewPolyproleneWizardScreen(client);
        }
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(PolyproleneConfig.class, GsonConfigSerializer::new);
        configuration = AutoConfig
                .getConfigHolder(PolyproleneConfig.class)
                .getConfig();
        LOGGER.info("Polyprolene is coming for your keybinds >:D");

        launchingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.polyprolene.launcher",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.polyprolene"
        ));

        favoriteKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.polyprolene.favorite",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.polyprolene"
        ));

        wizardkey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.polyprolene.wizard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.polyprolene"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(PolyproleneClient::registerScreensForDisplay);

        ClientLifecycleEvents.CLIENT_STARTED.register(t -> AutoCompleteResult.loadDataFromFile());
        ClientLifecycleEvents.CLIENT_STOPPING.register(t -> AutoCompleteResult.saveDataToFiles());

        AutoCompleteResult.suggestionProviders.add(createVanillaKeyBindingSuggestions());
    }
}
