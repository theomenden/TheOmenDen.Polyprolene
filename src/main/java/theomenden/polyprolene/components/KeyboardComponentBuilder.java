package theomenden.polyprolene.components;

import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.enums.MouseButtons;
import theomenden.polyprolene.utils.MathUtils;

public class KeyboardComponentBuilder {

    private static final int[] FUNCTION_KEYS = new int[]{
            GLFW.GLFW_KEY_F1,
            GLFW.GLFW_KEY_F2,
            GLFW.GLFW_KEY_F3,
            GLFW.GLFW_KEY_F4,
            GLFW.GLFW_KEY_F5,
            GLFW.GLFW_KEY_F6,
            GLFW.GLFW_KEY_F7,
            GLFW.GLFW_KEY_F8,
            GLFW.GLFW_KEY_F9,
            GLFW.GLFW_KEY_F10,
            GLFW.GLFW_KEY_F11,
            GLFW.GLFW_KEY_F12
    };

    private static final int[] NUMERIC_KEYS = new int[]{
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            GLFW.GLFW_KEY_1,
            GLFW.GLFW_KEY_2,
            GLFW.GLFW_KEY_3,
            GLFW.GLFW_KEY_4,
            GLFW.GLFW_KEY_5,
            GLFW.GLFW_KEY_6,
            GLFW.GLFW_KEY_7,
            GLFW.GLFW_KEY_8,
            GLFW.GLFW_KEY_9,
            GLFW.GLFW_KEY_0,
            GLFW.GLFW_KEY_MINUS,
            GLFW.GLFW_KEY_EQUAL
    };

    private static final int[] QWERTY_KEYS = new int[]{
            GLFW.GLFW_KEY_Q,
            GLFW.GLFW_KEY_W,
            GLFW.GLFW_KEY_E,
            GLFW.GLFW_KEY_R,
            GLFW.GLFW_KEY_T,
            GLFW.GLFW_KEY_Y,
            GLFW.GLFW_KEY_U,
            GLFW.GLFW_KEY_I,
            GLFW.GLFW_KEY_O,
            GLFW.GLFW_KEY_P,
            GLFW.GLFW_KEY_LEFT_BRACKET,
            GLFW.GLFW_KEY_RIGHT_BRACKET
    };

    private static final int[] MIDROW_KEYS = new int[]{
            GLFW.GLFW_KEY_A,
            GLFW.GLFW_KEY_S,
            GLFW.GLFW_KEY_D,
            GLFW.GLFW_KEY_F,
            GLFW.GLFW_KEY_G,
            GLFW.GLFW_KEY_H,
            GLFW.GLFW_KEY_J,
            GLFW.GLFW_KEY_K,
            GLFW.GLFW_KEY_L,
            GLFW.GLFW_KEY_SEMICOLON,
            GLFW.GLFW_KEY_APOSTROPHE
    };

    private static final int[] LOWER_KEYS = new int[]{
            GLFW.GLFW_KEY_Z,
            GLFW.GLFW_KEY_X,
            GLFW.GLFW_KEY_C,
            GLFW.GLFW_KEY_V,
            GLFW.GLFW_KEY_B,
            GLFW.GLFW_KEY_N,
            GLFW.GLFW_KEY_M,
            GLFW.GLFW_KEY_COMMA,
            GLFW.GLFW_KEY_PERIOD,
            GLFW.GLFW_KEY_SLASH
    };

    private static final int[] FORM_KEYS = new int[]{
            GLFW.GLFW_KEY_LEFT_CONTROL,
            GLFW.GLFW_KEY_LEFT_SUPER,
            GLFW.GLFW_KEY_LEFT_ALT,
            GLFW.GLFW_KEY_SPACE,
            GLFW.GLFW_KEY_RIGHT_ALT,
            GLFW.GLFW_KEY_RIGHT_SUPER,
            GLFW.GLFW_KEY_RIGHT_CONTROL
    };

    public static KeyboardComponent buildStandardKeyBoard(PolyproleneKeyboardScreen screen, float anchorX, float anchorY, float width, float height) {
        KeyboardComponent kb = new KeyboardComponent(screen, anchorX, anchorY);

        float currentX;
        float currentY = 0;

        float keySpacings = 5;
        float keyWidths = width * MathUtils.INV_12 - keySpacings;
        float keyHeight = height * MathUtils.INV_6 - keySpacings;

        currentX = addUniformHorizontalRow(kb, FUNCTION_KEYS, 0, currentY, keyWidths, keyHeight, keySpacings);

        currentY += keyHeight + keySpacings;
        keyWidths = width * MathUtils.INV_15 - keySpacings;
        currentX = addUniformHorizontalRow(kb, NUMERIC_KEYS, 0, currentY, keyWidths, keyHeight, keySpacings);
        currentX = kb.addKeyToMap(currentX, currentY, (keyWidths * 2 + keySpacings), keyHeight, keySpacings, GLFW.GLFW_KEY_BACKSPACE);

        currentY += keyHeight + keySpacings;
        currentX = kb.addKeyToMap(0, currentY, keyWidths * 2 + keySpacings, keyHeight, keySpacings, GLFW.GLFW_KEY_TAB);
        currentX = addUniformHorizontalRow(kb, QWERTY_KEYS, currentX, currentY, keyWidths, keyHeight, keySpacings);
        currentX = kb.addKeyToMap(currentX, currentY, keyWidths, keyHeight, keySpacings, GLFW.GLFW_KEY_BACKSLASH);

        currentY += keyHeight + keySpacings;
        currentX = kb.addKeyToMap(0, currentY, keyWidths * 2 + keySpacings, keyHeight, keySpacings, GLFW.GLFW_KEY_CAPS_LOCK);
        currentX = addUniformHorizontalRow(kb, MIDROW_KEYS, currentX, currentY, keyWidths, keyHeight, keySpacings);
        kb.addKeyToMap(currentX, currentY, (keyWidths * 2 + keySpacings), keyHeight, keySpacings, GLFW.GLFW_KEY_ENTER);

        currentY += keyHeight + keySpacings;
        currentX = kb.addKeyToMap(0, currentY, keyWidths * 2 + keySpacings, keyHeight, keySpacings, GLFW.GLFW_KEY_LEFT_SHIFT);
        currentX = addUniformHorizontalRow(kb, LOWER_KEYS, currentX, currentY, keyWidths, keyHeight, keySpacings);
        currentX = kb.addKeyToMap(currentX, currentY, (keyWidths * 3 + keySpacings * 2), keyHeight, keySpacings, GLFW.GLFW_KEY_RIGHT_SHIFT);

        currentY += keyHeight + keySpacings;
        keyWidths = width / 7 - keySpacings;
        currentX = addUniformHorizontalRow(kb, FORM_KEYS, 0, currentY, keyWidths, keyHeight, keySpacings);

        return kb;
    }

    public static KeyboardComponent buildSingleKeyKeyboard(PolyproleneKeyboardScreen screen, float anchorX, float anchorY, float width, float height, int keyCode, InputUtil.Type keyType) {
        KeyboardComponent kb = new KeyboardComponent(screen, anchorX, anchorY);
        kb.addKeyToMap(0f, 0f, width, height, 0f, keyCode, keyType);
        return kb;
    }

    public static KeyboardComponent buildSingleKeyKeyboard(PolyproleneKeyboardScreen screen, float anchorX, float anchorY, float width, float height, MouseButtons mouseKey, InputUtil.Type keyType) {
        return buildSingleKeyKeyboard(screen, anchorX, anchorY, width, height, mouseKey.getGLFWValue(), keyType);
    }

    private static float addUniformHorizontalRow(KeyboardComponent kb, int[] keys, float startX, float y, float width, float height, float spacing) {
        float currentX = startX;

        for (int k : keys) {
            currentX = kb.addKeyToMap(currentX, y, width, height, spacing, k);
        }
        
        return currentX;
    }
}
