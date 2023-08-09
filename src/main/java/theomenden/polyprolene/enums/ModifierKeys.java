package theomenden.polyprolene.enums;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.interfaces.IKeyConflictDeterminator;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public enum ModifierKeys implements StringIdentifiable {
    CONTROL {
        @Override
        public String asString() {
            return "CONTROL";
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            int keyCode = key.getCode();
            if (MinecraftClient.IS_SYSTEM_MAC) {
                return keyCode == GLFW.GLFW_KEY_LEFT_ALT
                        || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
            }

            return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
        }

        @Override
        public boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator) {
            return Screen.hasControlDown();
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return Text.literal("Key code: " + key + defaultedValue.get());
        }

    },
    SHIFT {
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_KEY_LEFT_SHIFT
                    || key.getCode() == GLFW.GLFW_KEY_RIGHT_SHIFT;

        }

        @Override
        public boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator) {
            return Screen.hasShiftDown();
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return Text.literal(defaultedValue
                    .get()
                    .getString());
        }

        @Override
        public String asString() {
            return "SHIFT";
        }
    },
    ALT {
        @Override
        public String asString() {
            return "ALT";
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_KEY_LEFT_ALT
                    || key.getCode() == GLFW.GLFW_KEY_RIGHT_ALT;
        }

        @Override
        public boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator) {
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
    NONE {
        @Override
        public String asString() {
            return "";
        }

        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return false;
        }

        @Override
        public boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MODIFIER_KEY_VALUES)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return defaultedValue.get();
        }
    };

    public static final ModifierKeys[] MODIFIER_KEY_VALUES = {SHIFT, CONTROL, ALT};

    public static ModifierKeys getActiveModifier() {
        return Arrays
                .stream(MODIFIER_KEY_VALUES)
                .filter(km -> km.isActivated(null))
                .findFirst()
                .orElse(NONE);
    }

    public static boolean isModifierForKeyCode(InputUtil.Key key) {
        return Arrays
                .stream(MODIFIER_KEY_VALUES)
                .anyMatch(km -> km.isMatchedBy(key));
    }

    public abstract boolean isMatchedBy(InputUtil.Key key);

    public abstract boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator);

    public abstract Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue);
}
