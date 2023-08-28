package theomenden.polyprolene.models.keyinfo;

import theomenden.polyprolene.enums.ModifierKey;

public final class UnsupportedModifiers extends ModifierKeys {
    public static final ModifierKeys INSTANCE = new UnsupportedModifiers();
    private static final String EXCEPTION = "This modifier cannot be altered";

    @Override
    public ModifierKeys setFlags(boolean[] value) {
        throw new UnsupportedOperationException(EXCEPTION);
    }

    @Override
    public void clearFlags() {
        throw new UnsupportedOperationException(EXCEPTION);
    }

    @Override
    public void set(ModifierKey modifierKey, boolean value) {
        throw new UnsupportedOperationException(EXCEPTION);
    }
}
