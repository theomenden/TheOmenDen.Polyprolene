package theomenden.polyprolene.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import theomenden.polyprolene.enums.ModifierKey;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;

import java.util.*;
import java.util.stream.Collectors;

public class KeyboardBindingsMap {
    private static final EnumMap<ModifierKey, Map<InputUtil.Key, Collection<KeyBinding>>> keyboardMapping = Maps.newEnumMap(ModifierKey.class);

    static {
        Arrays
                .stream(ModifierKey.values())
                .forEach(mk -> keyboardMapping.put(mk, Maps.newHashMap()));
    }

    public static void addBinding(InputUtil.Key key, KeyBinding binding) {
        ModifierKey modifier = ((IKeyBindingHandler) binding).getModifier();
        var bindingsMap = keyboardMapping.get(modifier);
        var currentKeyBindings = bindingsMap.computeIfAbsent(key,
                k -> Lists.newArrayList());

        currentKeyBindings.add(binding);
    }

    public static void removeBinding(KeyBinding binding) {
        final IKeyBindingHandler handler = (IKeyBindingHandler) binding;
        ModifierKey modifierKey = handler.getModifier();
        InputUtil.Key keyCode = handler.getKey();

        var bindingsMap = keyboardMapping.get(modifierKey);
        var currentKeyBindings = bindingsMap.get(keyCode);

        if (currentKeyBindings != null) {
            currentKeyBindings.remove(binding);

            if (currentKeyBindings.isEmpty()) {
                bindingsMap.remove(binding);
            }
        }
    }

    public static void clearKeyboardMap() {
        keyboardMapping
                .values()
                .forEach(Map::clear);
    }

    private static Set<KeyBinding> getBindings(InputUtil.Key key, ModifierKey modifierKey) {
        var bindings = keyboardMapping
                .get(modifierKey)
                .get(key);

        if (bindings == null || bindings.isEmpty()) {
            return Sets.newHashSet();
        }

        return bindings
                .stream()
                .filter(binding -> ((IKeyBindingHandler) binding).isActiveWithMatches(key))
                .collect(Collectors.toSet());
    }

    public List<KeyBinding> getAllBindingsForKeyCode(InputUtil.Key keyCode) {
        return keyboardMapping
                .values()
                .stream()
                .map(bindingsMap -> bindingsMap.get(keyCode))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Nullable
    public KeyBinding getActiveKeyBindingFromCode(InputUtil.Key keyCode) {
        final ModifierKey active = ModifierKey.getActiveModifier();

        if (!active.isMatchedBy(keyCode)) {
            var binding = getBinding(keyCode, active);
            if (binding != null) {
                return binding;
            }
        }
        return getBinding(keyCode, ModifierKey.NONE);
    }

    @Nullable
    private KeyBinding getBinding(InputUtil.Key key, ModifierKey modifier) {
        var bindings = keyboardMapping
                .get(modifier)
                .get(key);

        if (bindings == null || bindings.isEmpty()) {
            return null;
        }

        return bindings
                .stream()
                .filter(binding -> ((IKeyBindingHandler) binding).isActiveWithMatches(key))
                .findFirst()
                .orElseGet(null);
    }
}
