package theomenden.polyprolene.utils;

import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theomenden.polyprolene.client.PolyproleneClient;
import theomenden.polyprolene.enums.ModifierKey;
import theomenden.polyprolene.interfaces.IKeyBindingExtensions;
import theomenden.polyprolene.interfaces.IKeyBindingHandler;
import theomenden.polyprolene.managers.KeyBindingsManager;
import theomenden.polyprolene.models.keyinfo.ModifierKeys;
import theomenden.polyprolene.models.keyinfo.PolyproleneKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Getter
@Environment(EnvType.CLIENT)
public final class KeyBindingUtil {

    @Getter
    private static final int SCROLL_UP = 512;
    @Getter
    private static final int SCROLL_DOWN = 513;
    @Getter
    private static final double lastAmountScrolled = 0.0d;
    private static final Logger LOGGER = LoggerFactory.getLogger(PolyproleneClient.MODID);
    private static Map<String, KeyBinding> keyBindingMap;

    private KeyBindingUtil() {
    }

    public static Map<String, KeyBinding> getKeyBindingMap() {
        if (keyBindingMap == null) {
            try {
                Method method = KeyBinding.class.getDeclaredMethod("polyprolene$getKeyBindingMap");
                method.setAccessible(true);
                keyBindingMap = (Map<String, KeyBinding>) method.invoke(null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Failed to get access to key bindings", e);
            }
        }
        return keyBindingMap;
    }

    public static boolean unregister(KeyBinding keyBinding) {
        return unregister(keyBinding.getTranslationKey());
    }

    public static boolean unregister(String id) {
        KeyBinding keyBinding = getKeyBindingMap()
                .remove(id);
        return KeyBindingsManager.unregister(keyBinding);
    }

    public static ModifierKeys getBoundModifierKeys(KeyBinding keyBinding) {
        return ((IKeyBindingExtensions) keyBinding).getModifiers();
    }

    public static ModifierKey getDefaultModifier(KeyBinding keyBinding) {

        if (keyBinding instanceof IKeyBindingHandler) {
            return ((IKeyBindingHandler) keyBinding).getDefaultModifier();
        }

        return ModifierKey.NONE;
    }

    public static ModifierKeys getModifiersInUse(KeyBinding keyBinding) {
        return ((IKeyBindingExtensions) keyBinding).getModifiers();
    }

    public static void resetBoundModifiers(KeyBinding keyBinding) {
        ((IKeyBindingExtensions) keyBinding)
                .getModifiers()
                .clearFlags();

        if (keyBinding instanceof PolyproleneKey) {
            ((PolyproleneKey) keyBinding).resetBinding();
        }
    }

    public static InputUtil.Key getScrollKey(double deltaY) {
        return InputUtil.Type.MOUSE.createFromCode(deltaY > 0 ?
                KeyBindingUtil.SCROLL_UP
                : KeyBindingUtil.SCROLL_DOWN);
    }
}
