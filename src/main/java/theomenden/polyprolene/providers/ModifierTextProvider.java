package theomenden.polyprolene.providers;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import theomenden.polyprolene.enums.ModifierKey;
import theomenden.polyprolene.enums.PrefixTextVariations;

public class ModifierTextProvider {
    private static final Text SUFFIX = Text.literal(" + ");
    private static final Text COMPRESSED_SUFFIX = Text.literal("+");
    private final String translationKey;

    public ModifierTextProvider(ModifierKey modifierKey) {
        this(modifierKey.name());
    }

    public ModifierTextProvider(String translationKey) {
        this.translationKey = translationKey;
    }

    protected MutableText getBaseText(PrefixTextVariations variations) {
        return MutableText.of(variations.getTranslatableText(translationKey));
    }

    public MutableText getText(PrefixTextVariations variations) {
        MutableText text = getBaseText(variations);

        text.append(
                variations == PrefixTextVariations.COMPRESSED
                        ? COMPRESSED_SUFFIX
                        : SUFFIX
        );

        return text;

    }
}
