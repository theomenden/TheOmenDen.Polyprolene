package theomenden.polyprolene.managers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import theomenden.polyprolene.client.PolyproleneScreen;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;
import theomenden.polyprolene.mixin.keys.KeyBindAccessor;
import theomenden.polyprolene.utils.LoggerUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyBindingsManager {
    private static final Map<InputUtil.Key, List<KeyBinding>> keyMappingConflicts = Maps.newHashMap();
    private static final ArrayListMultimap<InputUtil.Key, KeyBinding> keyMappingFix = ArrayListMultimap.create();
    private static final Map<InputUtil.Key, List<KeyBinding>> priorityKeysById = new HashMap<>();
    private static final List<KeyBinding> pressedKeys = new ArrayList<>(10);

    private KeyBindingsManager() {
    }

    public static void addKeyToFixingMap(InputUtil.Key key, KeyBinding keyBinding) {
        if (isConflictingKeyBind(key)) {
            LoggerUtils
                    .getLoggerInstance()
                    .info("Key " + key.getLocalizedText() + " was found to have a conflicting mapping");

        }
        keyMappingFix.put(key, keyBinding);
    }

    public static void handleKeyPressed(InputUtil.Key key) {
        keyMappingFix
                .entries()
                .stream()
                .filter(k -> k
                        .getKey()
                        .equals(key))
                .forEach(k -> {
                    var keyBindAccessor = ((KeyBindAccessor) k.getValue());

                    keyBindAccessor.setTimesPressed(keyBindAccessor.getTimesPressed() + 1);
                });
    }

    public static void setKeyAsPressed(InputUtil.Key key, boolean isPressed) {
        keyMappingFix
                .entries()
                .stream()
                .filter(k -> key.equals(k.getKey()))
                .forEach(
                        k -> k
                                .getValue()
                                .setPressed(isPressed)
                );
    }

    public static boolean unregister(KeyBinding keyBinding) {
        if (keyBinding == null) {
            return false;
        }

        return removeKeyBinding(keyMappingConflicts, keyBinding)
                | removeKeyBinding(priorityKeysById, keyBinding);
    }

    public static void clearMappingFixes() {
        keyMappingFix.clear();
    }

    public static boolean isConflictingKeyBind(InputUtil.Key keyToCheck) {
        return keyMappingConflicts.containsKey(keyToCheck);
    }

    public static boolean shouldHandleConflictingKeyBinding(InputUtil.Key keyToCheck) {
        var matches = listConflictingKeyBindsForInputKey(keyToCheck);

        if (matches.size() > 1) {
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
        PolyproleneScreen keyMappingsScreen = new PolyproleneScreen();
        keyMappingsScreen.setConflictedKey(conflictingKey);
        MinecraftClient
                .getInstance()
                .setScreen(keyMappingsScreen);
    }

    public static Stream<KeyBinding> getConflictingBindings(InputUtil.Key keyCode, boolean isPriority) {
        List<KeyBinding> keyBindingList = (isPriority ? priorityKeysById : keysById).get(keyCode);
        if (keyBindingList == null) {
            return Stream.empty();
        }
        Stream<KeyBinding> result = keyBindingList
                .stream()
                .filter(keyBinding -> ((IKeyBinding) keyBinding)
                        .amecs$getKeyModifiers()
                        .isPressed());
        List<KeyBinding> keyBindings = result.collect(Collectors.toList());
        if (keyBindings.isEmpty()) {
            return keyBindingList
                    .stream()
                    .filter(keyBinding -> ((IKeyBinding) keyBinding)
                            .amecs$getKeyModifiers()
                            .isUnset());
        }
        return keyBindings.stream();
    }

    private static List<KeyBinding> listConflictingKeyBindsForInputKey(InputUtil.Key key) {
        KeyBinding[] allKeys = MinecraftClient.getInstance().options.allKeys;

        return Arrays
                .stream(allKeys)
                .filter(bind -> bind.matchesKey(key.getCode(), -1))
                .collect(Collectors.toCollection(Lists::newArrayList));
    }

    private static boolean removeKeyBinding(Map<InputUtil.Key, List<KeyBinding>> target, KeyBinding bindingToRemove) {
        InputUtil.Key keyCode = ((IKeyBindingHandler) bindingToRemove).getKey();
        List<KeyBinding> keyBindings = target.get(keyCode);

        if (keyBindings == null || keyBindings.isEmpty()) {
            return false;
        }

        boolean wasRemoved = false;
        while (keyBindings.remove(bindingToRemove)) {
            wasRemoved = true;
        }
        return wasRemoved;
    }
}
