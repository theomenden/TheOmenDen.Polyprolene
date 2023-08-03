package theomenden.polyprolene.client;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PolyproleneClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    public static final String MODID = "polyprolene";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Polyprolene is coming for your keybinds ⍉");
    }
}
