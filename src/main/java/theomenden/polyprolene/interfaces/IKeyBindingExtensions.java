package theomenden.polyprolene.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import theomenden.polyprolene.models.keyinfo.ModifierKeys;

@Environment(EnvType.CLIENT)
public interface IKeyBindingExtensions {
    InputUtil.Key getBoundedKey();

    int getTimesPressed();

    void setTimesPressed(int timesPressed);

    void incrementTimesPressed();

    void resetBinding();

    ModifierKeys getModifiers();
}
