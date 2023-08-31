package theomenden.polyprolene.managers;

import de.siphalor.amecs.impl.KeyBindingManager;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.compress.utils.Lists;
import theomenden.polyprolene.client.PolyproleneScreen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class KeyBindingsManager extends KeyBindingManager {
    @Getter
    private static final Map<InputUtil.Key, List<KeyBinding>> conflictingKeys = new HashMap<>();

    public static boolean shouldHandleConflictingKeyBinding(InputUtil.Key key) {
        KeyBinding[] keysAll = MinecraftClient.getInstance().options.allKeys;

        List<KeyBinding> matches = Arrays
                .stream(keysAll)
                .filter(bind -> bind.matchesKey(key.getCode(), -1))
                .collect(Collectors.toList());

        if (matches.size() > 1) {
            KeyBindingsManager.conflictingKeys.put(key, matches);
            return true;
        }
        KeyBindingsManager.conflictingKeys.remove(key);
        return false;
    }

    public static boolean isConflictingKeyBind(InputUtil.Key key) {
        return conflictingKeys.containsKey(key);
    }

    public static void openConflictingKeyBindingsScreen(InputUtil.Key key) {
        PolyproleneScreen screen = new PolyproleneScreen();
        screen.setConflictedKey(key);
        MinecraftClient
                .getInstance()
                .setScreen(screen);
    }

    public static List<KeyBinding> getConflictingBindingsForKey(InputUtil.Key key) {
        if (conflictingKeys.containsKey(key)) {
            return conflictingKeys.get(key);
        }
        return Lists.newArrayList();
    }
}
