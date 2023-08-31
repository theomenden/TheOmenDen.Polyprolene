package theomenden.polyprolene.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = PolyproleneClient.MODID)
public class PolyproleneConfig implements ConfigData {
    @ConfigEntry.Category("title")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideCurrentlyBoundKeys = false;
    @ConfigEntry.Category("title")
    @ConfigEntry.Gui.Tooltip()
    public boolean nonConflictingKeys = false;
    @ConfigEntry.Category("title")
    @ConfigEntry.Gui.Tooltip()
    public int maximumAutoSuggestions = 5;

    @ConfigEntry.Category("title")
    @ConfigEntry.Gui.Tooltip()
    public boolean shouldShowRadial = false;

    @ConfigEntry.Gui.Excluded
    public double launcherX = 0;
    @ConfigEntry.Gui.Excluded
    public double launcherY = 0;

    @ConfigEntry.Category("title")
    @ConfigEntry.Gui.Tooltip
    public double backgroundOpacity = 0.7;
}
