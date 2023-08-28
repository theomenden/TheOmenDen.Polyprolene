package theomenden.polyprolene.enums;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.interfaces.IKeyConflictDeterminator;
import theomenden.polyprolene.providers.ModifierTextProvider;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public enum ModifierKey implements StringIdentifiable {
    NONE("none", -1) {
        @Override
        public String asString() {
            return name;
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return false;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return false;
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return defaultedValue.get();
        }
    },
    ALT("alt", GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT) {
        @Override
        public String asString() {
            return null;
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            int keyCode = key.getCode();

            return Arrays
                    .stream(keyCodes)
                    .anyMatch(k -> k == keyCode);
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return Screen.hasAltDown();
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    SHIFT("shift", GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT) {
        @Override
        public String asString() {
            return null;
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            int keyCode = key.getCode();

            return Arrays
                    .stream(keyCodes)
                    .anyMatch(k -> k == keyCode);
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return Screen.hasShiftDown();
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    CONTROL("control", GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL) {
        @Override
        public String asString() {
            return name.toUpperCase();
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            int keyCode = key.getCode();
            if (MinecraftClient.IS_SYSTEM_MAC) {
                return keyCode == GLFW.GLFW_KEY_LEFT_ALT
                        || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
            }

            return Arrays
                    .stream(keyCodes)
                    .anyMatch(k -> k == keyCode);
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return Screen.hasControlDown();
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    };
    public static final ModifierKey[] MODIFIER_KEY_VALUES = ModifierKey.values();

    public final String name;
    public final int id;
    public final ModifierTextProvider textProvider;

    final int[] keyCodes;

    ModifierKey(String name, int id, int... keyCodes) {
        this.name = name;
        this.id = id;
        textProvider = new ModifierTextProvider(this);
        this.keyCodes = keyCodes;
    }

    public static ModifierKey getActiveModifier() {
        return Arrays
                .stream(MODIFIER_KEY_VALUES)
                .filter(km -> km.isActivated(null))
                .findFirst()
                .orElse(NONE);
    }

    public static ModifierKey fromKey(InputUtil.Key key) {
        if (key == null
                || key.getCategory() != InputUtil.Type.KEYSYM) {
            return NONE;
        }

        return fromKeyCode(key.getCode());
    }

    public static ModifierKey fromKeyCode(int keyCode) {
        return Arrays
                .stream(MODIFIER_KEY_VALUES)
                .filter(km -> km != ModifierKey.NONE && km.id == keyCode)
                .findFirst()
                .orElse(ModifierKey.NONE);
    }

    public static boolean isModifierForKeyCode(InputUtil.Key key) {
        return Arrays
                .stream(MODIFIER_KEY_VALUES)
                .anyMatch(km -> km.isMatchedBy(key));
    }

    public static int getTotalModifiers() {
        return MODIFIER_KEY_VALUES.length - 1;
    }


    public abstract boolean isMatchedBy(InputUtil.Key key);

    public abstract boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator);

    public abstract Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue);
}
