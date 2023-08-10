package theomenden.polyprolene.enums;

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.interfaces.IKeyConflictDeterminator;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public enum MouseButtons implements StringIdentifiable {
    MOUSE_1{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_1;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_2{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_2;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_3{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_3;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_4{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_4;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_5{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_5;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_6{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_6;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_7{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_7;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    MOUSE_8{
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return key.getCode() == GLFW.GLFW_MOUSE_BUTTON_8;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return  Text.literal(key
                    .getLocalizedText()
                    .getString() + defaultedValue
                    .get()
                    .getString());
        }
    },
    NONE {
        @Override
        public boolean isMatchedBy(InputUtil.Key key) {
            return false;
        }

        @Override
        public boolean isActivated(@org.jetbrains.annotations.Nullable IKeyConflictDeterminator conflictDeterminator) {
            return conflictDeterminator != null
                    && !conflictDeterminator.isAConflictWith(KeyBindingConflicts.IN_GAME)
                    && Arrays
                    .stream(MOUSE_BUTTONS)
                    .anyMatch(mk -> mk.isActivated(null));
        }

        @Override
        public Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue) {
            return defaultedValue.get();
        }
    };

    @Override
    public String asString() {
        return this.name();
    }

    public static final MouseButtons[] MOUSE_BUTTONS = {MOUSE_1, MOUSE_2, MOUSE_3, MOUSE_4, MOUSE_5, MOUSE_6, MOUSE_7, MOUSE_8};

    public static MouseButtons getActiveMouseButton() {
        return Arrays
                .stream(MOUSE_BUTTONS)
                .filter(km -> km.isActivated(null))
                .findFirst()
                .orElse(NONE);
    }

    public static boolean isMouseButtonForKeyCode(InputUtil.Key key) {
        return Arrays
                .stream(MOUSE_BUTTONS)
                .anyMatch(km -> km.isMatchedBy(key));
    }

    public abstract boolean isMatchedBy(InputUtil.Key key);

    public abstract boolean isActivated(@Nullable IKeyConflictDeterminator conflictDeterminator);

    public abstract Text getLocalizedName(InputUtil.Key key, Supplier<Text> defaultedValue);
}
