package theomenden.polyprolene.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "polyprolene")
public class PolyproleneConfig implements ConfigData {
    public boolean hideCurrentlyBoundKeys = false;
    public int maximumAutoSuggestions = 5;

    @ConfigEntry.Gui.Excluded
    public double launcherX = 0;
    @ConfigEntry.Gui.Excluded
    public double launcherY = 0;

    public double backgroundOpacity = 0.7;
}
