package theomenden.polyprolene.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import theomenden.polyprolene.models.KeyBindSuggestion;

import java.util.List;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ISuggestionProvider {
    void addKeyBindingSuggestions(List<KeyBindSuggestion> bindings);
}
