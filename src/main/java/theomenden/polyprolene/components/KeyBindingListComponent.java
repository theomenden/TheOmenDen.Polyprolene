package theomenden.polyprolene.components;

import me.shedaniel.clothconfig2.api.TickableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.mixin.keys.KeyBindAccessor;
import theomenden.polyprolene.utils.KeyInfoUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyBindingListComponent extends FreeFormListComponent<KeyBindingListComponent.BindingEntry> implements TickableWidget {
    public PolyproleneKeyboardScreen screen;
    private String currentFilterText = "";
    private String currentCategory = KeyInfoUtils.DYNAMIC_CATEGORIES;

    public KeyBindingListComponent(PolyproleneKeyboardScreen screen, int top, int left, int width, int height, int itemHeight) {
        super(MinecraftClient.getInstance(), width, height, top, left, itemHeight);
        this.screen = screen;

        Arrays
                .stream(this.client.options.allKeys)
                .map(BindingEntry::new)
                .forEach(this::addEntry);

        this.setSelected(this
                .children()
                .get(0));
    }

    private static boolean extracted(KeyBinding binding, String[] words) {
        boolean flag = true;
        for (String w : words) {
            flag = flag && I18n
                    .translate(binding.getTranslationKey())
                    .toLowerCase()
                    .contains(w.toLowerCase());
        }

        return flag;
    }

    @Override
    public void tick() {

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Nullable
    public KeyBinding getSelectedKeyBinding() {
        if (this.getSelectedOrNull() == null) {
            return null;
        }
        return ((BindingEntry) this.getSelectedOrNull()).keyBinding;
    }

    private void updateList() {
        boolean isFilterUpdate = !this.currentFilterText.equals(this.screen.getFilteredText());
        boolean isCategoryUpdate = !this.currentCategory.equals(this.screen.getSelectedCategory());

        if (!isCategoryUpdate && !isFilterUpdate) {
            return;
        }

        if (isCategoryUpdate) {
            this.currentCategory = this.screen.getSelectedCategory();
        }

        KeyBinding[] bindings = getBindingsByCategory(this.currentCategory);

        if (isFilterUpdate) {
            this.currentFilterText = this.screen.getFilteredText();

            if (!this.currentFilterText.equals("")) {
                bindings = filterBindings(bindings, this.currentFilterText);
            }
        }

        this
                .children()
                .clear();

        if (bindings.length > 0) {
            Arrays
                    .stream(bindings)
                    .map(BindingEntry::new)
                    .forEach(this::addEntry);

            this.setSelected(this
                    .children()
                    .get(0));
        } else {
            this.setSelected(null);
        }
        this.setScrollAmount(0);
    }

    private KeyBinding[] filterBindings(KeyBinding[] bindings, String filterText) {
        KeyBinding[] bindingsFiltered = bindings;
        String keyNameRegex = "<.*>";
        Matcher keyNameMatcher = Pattern
                .compile(keyNameRegex)
                .matcher(filterText);


        if (keyNameMatcher.find()) {
            String keyNameWithBrackets = keyNameMatcher.group();
            String keyName = keyNameWithBrackets
                    .replace("<", "")
                    .replace(">", "");
            filterText = filterText.replace(keyNameWithBrackets, "");
            bindingsFiltered = filterBindingsByKey(bindingsFiltered, keyName);
        }

        if (!filterText.equals("")) {
            bindingsFiltered = filterBindingsByName(bindingsFiltered, filterText);
        }

        return bindingsFiltered;
    }

    private KeyBinding[] filterBindingsByName(KeyBinding[] bindings, String bindingName) {
        String[] words = bindingName.split("\\s+");
        KeyBinding[] bindingsFiltered = Arrays
                .stream(bindings)
                .filter(binding -> extracted(binding, words))
                .toArray(KeyBinding[]::new);
        return bindingsFiltered;
    }

    private KeyBinding[] filterBindingsByKey(KeyBinding[] bindings, String keyName) {
        return Arrays
                .stream(bindings)
                .filter(b -> {
                    Text t = b.getBoundKeyLocalizedText();

                    return I18n
                            .translate(t.getString())
                            .equalsIgnoreCase(keyName)
                            || t
                            .getString()
                            .equalsIgnoreCase(keyName);

                })
                .toArray(KeyBinding[]::new);
    }

    private KeyBinding[] getBindingsByCategory(String category) {
        KeyBinding[] bindings = Arrays.copyOf(this.client.options.allKeys, this.client.options.allKeys.length);
        switch (category) {
            case KeyInfoUtils.DYNAMIC_CATEGORIES:
                return bindings;
            case KeyInfoUtils.DYNAMIC_CATEGORIES_WITH_CONFLICTS:
                Map<InputUtil.Key, Integer> bindingCounts = KeyInfoUtils.getGroupedKeyBindingsByKey();
                return Arrays
                        .stream(bindings)
                        .filter(b -> bindingCounts.get(((KeyBindAccessor) b).getBoundKey()) > 1 && ((KeyBindAccessor) b)
                                .getBoundKey()
                                .getCode() != -1)
                        .toArray(KeyBinding[]::new);
            case KeyInfoUtils.DYNAMIC_CATEGORIES_UNBOUND:
                return Arrays
                        .stream(bindings)
                        .filter(b -> b.isUnbound())
                        .toArray(KeyBinding[]::new);
            default:
                return Arrays
                        .stream(bindings)
                        .filter(b -> b.getCategory() == category)
                        .toArray(KeyBinding[]::new);
        }
    }

    public class BindingEntry extends FreeFormListComponent<KeyBindingListComponent.BindingEntry>.Entry {
        private final KeyBinding keyBinding;

        public BindingEntry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawTextWithShadow(client.textRenderer, Text.translatable(this.keyBinding.getTranslationKey()), x, y, 0xFFFFFFFF);
            int color = 0xFF999999;
            context.drawTextWithShadow(client.textRenderer, this.keyBinding.getBoundKeyLocalizedText(), x, y + client.textRenderer.fontHeight + 5, color);
        }
    }
}
