package theomenden.polyprolene.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import theomenden.polyprolene.mixin.keys.KeyBindAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyInfoUtils {
    public static final String DYNAMIC_CATEGORIES = "key.categories.polyprolene.all";
    public static final String DYNAMIC_CATEGORIES_WITH_CONFLICTS = "key.categories.polyprolene.conflicts";
    public static final String DYNAMIC_CATEGORIES_UNBOUND = "key.categories.polyprolene.unbound";

    public static List<String> getCategories() {
        return KeyBindAccessor
                .getKEY_CATEGORIES()
                .stream()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<String> getAppendedDynamicCategories() {
        var categories = getCategories();

        categories.add(0, DYNAMIC_CATEGORIES_UNBOUND);
        categories.add(0, DYNAMIC_CATEGORIES_WITH_CONFLICTS);
        categories.add(0, DYNAMIC_CATEGORIES);

        return categories;
    }

    public static Map<InputUtil.Key, Integer> getGroupedKeyBindingsByKey() {
        return Arrays
                .stream(MinecraftClient.getInstance().options.allKeys)
                .collect(Collectors.toMap(kb -> ((KeyBindAccessor) kb).getBoundKey(), kb -> 1, Integer::sum, ImmutableMap::ofEntries));
    }
}
