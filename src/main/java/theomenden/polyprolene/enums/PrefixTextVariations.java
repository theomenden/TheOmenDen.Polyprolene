package theomenden.polyprolene.enums;

import net.minecraft.text.TranslatableTextContent;

public enum PrefixTextVariations {
    COMPRESSED(".tiny"),
    TINY(".tiny"),
    SHORT(".short"),
    NORMAL("");

    public static final PrefixTextVariations[] VALUES = PrefixTextVariations.values();
    public static final PrefixTextVariations WIDEST = NORMAL;
    public static final PrefixTextVariations SMALLEST = COMPRESSED;

    public final String translateKeySuffix;

    PrefixTextVariations(String translateKeySuffix) {
        this.translateKeySuffix = translateKeySuffix;
    }

    public TranslatableTextContent getTranslatableText(String translationKey) {
        return new TranslatableTextContent(translationKey + translateKeySuffix, null, new Object[0]);
    }

    public PrefixTextVariations getNextVariation(int amount) {
        int targetOrdinal = ordinal() + amount;

        if (targetOrdinal < 0 || targetOrdinal >= VALUES.length) {
            return null;
        }

        return VALUES[targetOrdinal];
    }

    public PrefixTextVariations getSmaller() {
        return getNextVariation(-1);
    }
}
