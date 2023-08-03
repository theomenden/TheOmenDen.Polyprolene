package theomenden.polyprolene.enums;

import net.minecraft.util.StringIdentifiable;

public enum KeyboardLayouts implements StringIdentifiable {
    QWERTY("QWERTY"),
    NUMPAD("NUMPAD"),
    AUXILIARY("AUX");

    private final String displayName;

    KeyboardLayouts(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String asString() {
        return this.displayName;
    }
}
