package theomenden.polyprolene.models.keyinfo;

import lombok.Getter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.enums.ModifierKey;
import theomenden.polyprolene.interfaces.IKeyBindingExtensions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
public class ModifierKeys {
    @Getter
    private final boolean[] value;

    public ModifierKeys() {
        this(new boolean[ModifierKey.MODIFIER_KEY_VALUES.length]);
    }

    public ModifierKeys(boolean usingAlt, boolean usingControl, boolean usingShift) {
        this();
        setAlt(usingAlt);
        setControl(usingControl);
        setShift(usingShift);
    }

    protected ModifierKeys(boolean[] value) {
        if (value.length != ModifierKey.MODIFIER_KEY_VALUES.length) {
            throw new IllegalArgumentException("");
        }
        this.value = value;
    }

    public static boolean[] deserialize(String value) {

        var result = new boolean[ModifierKey.values().length];
        if (value.isBlank()) {
            return result;
        }

        List<Boolean> list = Stream
                .of(value.split(","))
                .map(v -> v.equals("1"))
                .toList();

        IntStream
                .range(0, list.size())
                .forEach(v -> result[v] = list.get(v));
        return result;
    }

    public void copy(ModifierKeys other) {

    }

    public boolean isPressed() {
        return equals(PolyproleneClient.CURRENT_MODIFIERS);
    }

    public ModifierKeys setFlags(boolean[] value) {
        int length = this.value.length;
        if (value.length != length) {
            throw new IllegalArgumentException("");
        }

        System.arraycopy(value, 0, this.value, 0, length);
        return this;
    }

    public void clearFlags() {
        Arrays.fill(value, false);
    }

    public void cleanupFlagBy(KeyBinding binding) {
        InputUtil.Key key = ((IKeyBindingExtensions) binding).getBoundedKey();
        set(ModifierKey.fromKey(key), false);
    }

    public void set(ModifierKey modifierKey, boolean value) {
        if (modifierKey != ModifierKey.NONE) {
            this.value[modifierKey.ordinal()] = value;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModifierKeys that = (ModifierKeys) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public String toString() {
        return "ModifierKeys{" +
                "value=" + Arrays.toString(value) +
                '}';
    }

    public ModifierKeys setControl(boolean value) {
        set(ModifierKey.CONTROL, value);
        return this;
    }

    public ModifierKeys setShift(boolean value) {
        set(ModifierKey.SHIFT, value);
        return this;
    }

    public ModifierKeys setAlt(boolean value) {
        set(ModifierKey.ALT, value);
        return this;
    }


    public boolean get(ModifierKey modifierKey) {
        boolean result = true;
        if (modifierKey != ModifierKey.NONE) {
            result = value[modifierKey.ordinal()];
        }
        return result;
    }
}
