package theomenden.polyprolene.enums;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.StringIdentifiable;
import theomenden.polyprolene.interfaces.IKeyConflictDeterminator;

public enum KeyBindingConflicts implements StringIdentifiable, IKeyConflictDeterminator {

    UNIVERSAL {
        @Override
        public boolean isACurrentActivelyKeyBinding() {
            return true;
        }

        @Override
        public boolean isAConflictWith(IKeyConflictDeterminator other) {
            return true;
        }
    },
    GUI {
        @Override
        public boolean isACurrentActivelyKeyBinding() {
            return MinecraftClient.getInstance().currentScreen != null;
        }

        @Override
        public boolean isAConflictWith(IKeyConflictDeterminator other) {
            return this == other;
        }

    },
    IN_GAME {
        @Override
        public boolean isACurrentActivelyKeyBinding() {
            return GUI.isACurrentActivelyKeyBinding();
        }

        @Override
        public boolean isAConflictWith(IKeyConflictDeterminator other) {
            return this == other;
        }
    };

    @Override
    public String asString() {
        return this.name();
    }
}
