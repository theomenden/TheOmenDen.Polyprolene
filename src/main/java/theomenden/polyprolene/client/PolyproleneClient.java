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
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.models.AutoCompleteResult;
import theomenden.polyprolene.models.VanillaKeyBindingSuggestions;
import theomenden.polyprolene.utils.ConfigurationUtils;
import theomenden.polyprolene.utils.LoggerUtils;

public class PolyproleneClient implements ClientModInitializer {

    public static PolyproleneConfig configuration;
    public static KeyBinding launchingKey;
    public static KeyBinding favoriteKey;

    public static final String MODID = "polyprolene";

    @Override
    public void onInitializeClient() {
        AutoConfig.register(PolyproleneConfig.class, GsonConfigSerializer::new);
        configuration = AutoConfig
                .getConfigHolder(PolyproleneConfig.class)
                .getConfig();
        LoggerUtils
                .getLoggerInstance()
                .info("Polyprolene is coming for your keybinds >:D");

        if (!ConfigurationUtils.isDirectoryReadyToBeWritten()) {
            ConfigurationUtils.createFile(ConfigurationUtils.getHistoryPath());
            ConfigurationUtils.createFile(ConfigurationUtils.getFavoritesPath());
        }

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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (launchingKey.wasPressed()) {
                openNewPolyproleneScreen(client);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(t -> AutoCompleteResult.loadDataFromFile());
        ClientLifecycleEvents.CLIENT_STOPPING.register(t -> AutoCompleteResult.saveDataToFiles());

        AutoCompleteResult.suggestionProviders.add(createVanillaKeyBindingSuggestions());
    }

    public VanillaKeyBindingSuggestions createVanillaKeyBindingSuggestions() {
        return new VanillaKeyBindingSuggestions();
    }

    public void openNewPolyproleneScreen(MinecraftClient client) {
        client.setScreen(new PolyproleneScreen());
    }
}
