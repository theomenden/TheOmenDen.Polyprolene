package theomenden.com.copolymerization.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import theomenden.com.copolymerization.client.CopolymerizationScreen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyBindingsManager {
    private static final Map<InputUtil.Key, List<KeyBinding>> keyMappingConflicts = Maps.newHashMap();

    public static boolean isConflictingKeyBind(InputUtil.Key keyToCheck) {
        return keyMappingConflicts.containsKey(keyToCheck);
    }
    public static boolean shouldHandleConflictingKeyBinding(InputUtil.Key keyToCheck) {
        var matches = listConflictingKeyBindsForInputKey(keyToCheck);

        if(matches.size() > 1) {
            KeyBindingsManager.keyMappingConflicts.put(keyToCheck, matches);
            return true;
        }

        KeyBindingsManager.keyMappingConflicts.remove(keyToCheck);
        return false;
    }
    public static List<KeyBinding> getConflictingBindingsForKey(InputUtil.Key keyToCheck) {
        return keyMappingConflicts.containsKey(keyToCheck)
                ? keyMappingConflicts.get(keyToCheck)
                : Lists.newArrayList();
    }
    public static void openConflictingKeyBindingsScreen(InputUtil.Key conflictingKey) {
        var keyMappingsScreen = new CopolymerizationScreen();
        keyMappingsScreen.setConflictedKeyBinding(conflictingKey);
        MinecraftClient
                .getInstance()
                .setScreen(keyMappingsScreen);
    }
    private static List<KeyBinding> listConflictingKeyBindsForInputKey(InputUtil.Key key) {
        var allKeys = MinecraftClient.getInstance().options.allKeys;

        return Arrays
                .stream(allKeys)
                .filter(bind -> bind.matchesKey(key.getCode(), -1))
                .collect(Collectors.toCollection(Lists::newArrayList));
    }
}
