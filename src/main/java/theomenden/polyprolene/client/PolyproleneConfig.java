package theomenden.polyprolene.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = PolyproleneClient.MODID)
public class PolyproleneConfig implements ConfigData {
    @ConfigEntry.Category("title")
    public boolean hideCurrentlyBoundKeys = false;
    @ConfigEntry.Category("title")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean nonConflictingKeys = false;
    @ConfigEntry.Category("title")
    public int maximumAutoSuggestions = 5;

    @ConfigEntry.Gui.Excluded
    public double launcherX = 0;
    @ConfigEntry.Gui.Excluded
    public double launcherY = 0;

    @ConfigEntry.Category("title")
    public double backgroundOpacity = 0.7;
}
