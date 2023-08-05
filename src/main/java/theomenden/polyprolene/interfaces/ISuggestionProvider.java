package theomenden.polyprolene.interfaces;

import theomenden.polyprolene.models.KeyBindSuggestion;

import java.util.List;

public interface ISuggestionProvider {

    void addKeyBindingSuggestions(List<KeyBindSuggestion> bindings);
}
