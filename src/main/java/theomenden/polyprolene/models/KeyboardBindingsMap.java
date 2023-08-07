package theomenden.polyprolene.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import theomenden.polyprolene.enums.ModifierKeys;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;

import java.util.*;
import java.util.stream.Collectors;

public class KeyboardBindingsMap {
    private static final EnumMap<ModifierKeys, Map<InputUtil.Key, Collection<KeyBinding>>> keyboardMapping = Maps.newEnumMap(ModifierKeys.class);

    static {
        Arrays.stream(ModifierKeys.values())
                .forEach(mk -> keyboardMapping.put(mk, Maps.newHashMap()));
    }

    public void addBinding(InputUtil.Key key, KeyBinding binding) {
        ModifierKeys modifier = ((IKeyBindingHandler)binding).getModifier();
        var bindingsMap = keyboardMapping.get(modifier);
        var currentKeyBindings = bindingsMap.computeIfAbsent(key,
                k -> Lists.newArrayList());

        currentKeyBindings.add(binding);
    }

    public void removeBinding(KeyBinding binding) {
        final IKeyBindingHandler handler = (IKeyBindingHandler) binding;
        ModifierKeys modifierKey = handler.getModifier();
        InputUtil.Key keyCode = handler.getKey();

        var bindingsMap = keyboardMapping.get(modifierKey);
        var currentKeyBindings = bindingsMap.get(keyCode);

        if(currentKeyBindings != null) {
            currentKeyBindings.remove(binding);

            if(currentKeyBindings.isEmpty()) {
                bindingsMap.remove(binding);
            }
        }
    }

    public void clearKeyboardMap() {
        keyboardMapping.values()
                .forEach(Map::clear);
    }

    private Set<KeyBinding> getBindings(InputUtil.Key key, ModifierKeys modifierKeys) {
        var bindings = keyboardMapping.get(modifierKeys).get(key);

        if(bindings == null || bindings.isEmpty()) {
            return Sets.newHashSet();
        }

        return bindings
                .stream()
                .filter(binding -> ((IKeyBindingHandler)binding).isActiveWithMatches(key))
                .collect(Collectors.toSet());
    }

    @Nullable
    private KeyBinding getBinding(InputUtil.Key key, ModifierKeys modifier) {
        var bindings = keyboardMapping.get(modifier).get(key);

        if(bindings == null || bindings.isEmpty()) {
            return null;
        }

        return bindings.stream()
                       .filter(binding -> ((IKeyBindingHandler)binding).isActiveWithMatches(key))
                       .findFirst()
                .orElseGet(null);
    }
}
