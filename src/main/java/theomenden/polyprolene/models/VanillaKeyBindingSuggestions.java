package theomenden.polyprolene.models;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.interfaces.ISuggestionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VanillaKeyBindingSuggestions implements ISuggestionProvider {
    private static final String[] DANGEROUS_BINDINGS = {
        "key.use",
                "key.attack",
                "key.forward",
                "key.left",
                "key.right",
                "key.back",
                "key.sneak",
                "key.sprint",
                "key.jump",
                "key.saveToolbarActivator",
                "key.loadToolbarActivator",
                "key.polyprolene.launcher",
                "key.polyprolene.favorite"
    };

    private static final Set<String> PREVENT_YOU_FROM_USING_THESE_BINDINGS = Set.of(DANGEROUS_BINDINGS);

    protected KeyBinding[] getAllKeys(GameOptions options){
        return options.allKeys;
    }
    protected KeyBindSuggestion createKeyBindSuggestion(KeyBinding binding) {
        return new KeyBindSuggestion(binding);
    }

    public void addKeyBindingSuggestions(List<KeyBindSuggestion> bindings) {
        bindings.addAll(Arrays.stream(getAllKeys(MinecraftClient.getInstance().options))
              .filter(bind ->
                      checkForUnboundOrHiddenBindings(bind)
              && PREVENT_YOU_FROM_USING_THESE_BINDINGS.contains(bind.getTranslationKey()))
              .map(this::createKeyBindSuggestion)
              .collect(Collectors.toList()));
    }

    private boolean checkForUnboundOrHiddenBindings(KeyBinding bind) {
        return bind.isUnbound() || PolyproleneClient.configuration.hideCurrentlyBoundKeys;
    }

}
