package theomenden.polyprolene.managers;

import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import theomenden.polyprolene.interfaces.IKeyInputEventHandler;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class KeyInputManager {
    private static final Set<IKeyInputEventHandler> INPUT_EVENT_HANDLERS = Sets.newLinkedHashSet();

    public static void handleEvents(MinecraftClient client) {
        INPUT_EVENT_HANDLERS
                .forEach(handler -> {
                    handler.handle(client);
                });
    }

    public static boolean registerInputHandler(IKeyInputEventHandler handler) {
        return INPUT_EVENT_HANDLERS.add(handler);
    }

    public static boolean unregisterInputHandler(IKeyInputEventHandler handler) {
        return INPUT_EVENT_HANDLERS.remove(handler);
    }
}
