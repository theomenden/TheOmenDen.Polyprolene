package theomenden.polyprolene.models.keyinfo;

import lombok.Getter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import theomenden.polyprolene.interfaces.IKeyBindingExtensions;

@Getter
public class PolyproleneKey extends KeyBinding {
    private final ModifierKeys defaults;

    public PolyproleneKey(Identifier id, InputUtil.Type type, int code, String category, ModifierKeys defaults) {
        this("key" + id.getNamespace() + "." + id.getPath(), type, code, category, defaults);
    }

    public PolyproleneKey(String id, InputUtil.Type type, int code, String category, ModifierKeys defaults) {
        super(id, type, code, category);

        if (defaults == null || defaults == UnsupportedModifiers.INSTANCE) {
            defaults = new ModifierKeys();
        }

        this.defaults = defaults;
        ((IKeyBindingExtensions) this)
                .getModifiers()
                .copy(this.defaults);
    }

    @Override
    public boolean isDefault() {
        return defaults
                .equals(((IKeyBindingExtensions) this).getModifiers())
                && super.isDefault();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (pressed) {
            onPressed();
            return;
        }
        onReleased();
    }

    public void onPressed() {

    }

    public void onReleased() {

    }

    public void resetBinding() {
        ((IKeyBindingExtensions) this)
                .getModifiers()
                .copy(defaults);
    }


}
