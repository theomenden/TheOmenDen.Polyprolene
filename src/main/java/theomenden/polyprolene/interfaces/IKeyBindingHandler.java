package theomenden.polyprolene.interfaces;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import theomenden.polyprolene.enums.ModifierKeys;

public interface IKeyBindingHandler {
  InputUtil.Key getKey();
  IKeyConflictDeterminator getConflictContext();
  ModifierKeys getDefaultModifier();
  ModifierKeys getModifier();
  void setKeyDeterminatorContext(IKeyConflictDeterminator determinatorContext);
  void setModifierWithCodeForKey(ModifierKeys keyModifier, InputUtil.Key key);
  void onPress();
  default boolean hasModifierKeyConflictInContext(KeyBinding other) {
     final IKeyBindingHandler extendedBinding = (IKeyBindingHandler) other;

     if(getConflictContext().isAConflictWith(extendedBinding.getConflictContext())
     || extendedBinding.getConflictContext().isAConflictWith(getConflictContext())) {
        return getModifier().isMatchedBy(extendedBinding.getKey())
                || extendedBinding.getModifier().isMatchedBy(getKey());
     }
     return false;
  }
  default boolean isActiveModifierWithContext() {
     return getConflictContext().isACurrentActivelyKeyBinding()
             && getModifier().isActivated(getConflictContext());
  }

  default boolean isActiveWithMatches(InputUtil.Key keyCode) {
      return keyCode != InputUtil.UNKNOWN_KEY
              && keyCode.equals(getKey())
              && getConflictContext().isACurrentActivelyKeyBinding()
              && getModifier().isActivated(getConflictContext());
  }

  default void setToDefault() {
     setModifierWithCodeForKey(getDefaultModifier(), getKeyBinding().getDefaultKey());
  }

  default KeyBinding getKeyBinding() {
     return (KeyBinding) this;
  }

}
