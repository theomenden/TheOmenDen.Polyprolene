package theomenden.polyprolene.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import org.apache.commons.lang3.Range;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.models.AutoCompleteResult;
import theomenden.polyprolene.models.KeyBindSuggestion;

import java.util.List;

public class PolyproleneScreen extends Screen {
    private final static int SUGGESTION_COLOR = 0x999999;
    private final static int HIGHLIGHT_COLOR = 0xFFFF00;

    public static double offsetX = 0d;
    public static double offsetY = 0d;
    public int configurableWidth = 250;
    public int lineHeight = 12;
    private int baseX;
    private int baseY;
    private TextFieldWidget textFieldWidget;
    private final AutoCompleteResult autoCompleteResult;
    private int selectedOption = 0;
    private int optionsOffset = 0;
    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;

    public PolyproleneScreen() {
        super(NarratorManager.EMPTY);
        autoCompleteResult = new AutoCompleteResult();
        autoCompleteResult.updateSuggestionsList("");
        offsetX = PolyproleneClient.configuration.launcherX;
        offsetY = PolyproleneClient.configuration.launcherY;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<KeyBindSuggestion> providedSuggestions = autoCompleteResult.getCurrentSuggestions();

        int totalAllowableSuggestions = Math.min(providedSuggestions.size(), PolyproleneClient.configuration.maximumAutoSuggestions);
        int backgroundColor = (int) Math.round(PolyproleneClient.configuration.backgroundOpacity * 255) * 0x1000000;
        context.fill(getFittedX() - 1, getFittedY() - 1, getFittedX() + configurableWidth - 1, getFittedY() + lineHeight - 2 + totalAllowableSuggestions * lineHeight, backgroundColor);
        textFieldWidget.setFocused(true);
        textFieldWidget.render(context, mouseX, mouseY, delta);

        int y = getFittedY();

        for (int i = optionsOffset; i - optionsOffset < PolyproleneClient.configuration.maximumAutoSuggestions; i++) {
            if (providedSuggestions.size() <= i) {
                break;
            }

            KeyBindSuggestion sg = providedSuggestions.get(i);

            y += lineHeight;
            if (sg.isAFavorite) {
                context.fill(getFittedX() - 3, y - 2, getFittedX() - 1, y + lineHeight - 2, HIGHLIGHT_COLOR | backgroundColor);
            }

            context.drawTextWithShadow(textRenderer, sg.name, getFittedX(), y, i == selectedOption ? HIGHLIGHT_COLOR : SUGGESTION_COLOR);

            int textWidth = textRenderer.getWidth(sg.category) + 2;

            context.drawTextWithShadow(textRenderer, sg.category, getFittedX() + configurableWidth - textWidth, y, SUGGESTION_COLOR);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (PolyproleneClient.favoriteKey.matchesKey(keyCode, scanCode)) {
            List<KeyBindSuggestion> keyBindSuggestions = autoCompleteResult.getCurrentSuggestions();

            if (keyBindSuggestions.size() > selectedOption) {
                AutoCompleteResult.toggleFavorite(keyBindSuggestions.get(selectedOption));
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_UP) {
            changeSelection(-1);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            changeSelection(1);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            List<KeyBindSuggestion> keyBindSuggestions = autoCompleteResult.getCurrentSuggestions();
            client.setScreen(null);

            if (keyBindSuggestions.size() > selectedOption) {
                keyBindSuggestions
                        .get(selectedOption)
                        .execute();
            }

            return true;
        }

        return textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
                || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        baseX = (width - configurableWidth) / 2;
        baseY = (height - lineHeight) / 2;

        String text = "";

        if (textFieldWidget != null) {
            text = textFieldWidget.getText();
        }

        textFieldWidget = new TextFieldWidget(textRenderer, getFittedX(), getFittedY() + 1, configurableWidth, lineHeight, NarratorManager.EMPTY);
        textFieldWidget.setDrawsBackground(false);
        textFieldWidget.setChangedListener(this::onTextChangedListener);
        textFieldWidget.setText(text);
        addSelectableChild(textFieldWidget);
        setInitialFocus(textFieldWidget);
    }

    @Override
    public void tick() {
        textFieldWidget.tick();
    }

    @Override
    public void removed() {
        PolyproleneClient.configuration.launcherX = offsetX;
        PolyproleneClient.configuration.launcherY = offsetY;
        AutoConfig
                .getConfigHolder(PolyproleneConfig.class)
                .save();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        init(client, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 1) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        offsetX = 0;
        offsetY = 0;
        init();
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!(Math.abs(offsetX) + Math.abs(offsetY) < 15)) {
            return false;
        }
        offsetY = 0;
        offsetX = 0;
        init();

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        offsetX = fitX(offsetX + deltaX + baseX) - baseX;
        offsetY = fitY(offsetY + deltaY + baseY) - baseY;
        init();
        return true;
    }

    public void onTextChangedListener(String s) {
        autoCompleteResult.updateSuggestionsList(s);
        selectedOption = 0;
        optionsOffset = 0;
    }

    public void changeSelection(int by) {
        int totalSuggestions = autoCompleteResult
                .getCurrentSuggestions()
                .size();

        if (totalSuggestions == 0) {
            selectedOption = 0;
            return;
        }

        selectedOption = ((selectedOption + by + totalSuggestions)) % totalSuggestions;

        if (optionsOffset > selectedOption) {
            optionsOffset = selectedOption;
            return;
        }

        if (optionsOffset + PolyproleneClient.configuration.maximumAutoSuggestions <= selectedOption) {
            optionsOffset = selectedOption - PolyproleneClient.configuration.maximumAutoSuggestions + 1;
        }
    }

    public void setConflictedKey(InputUtil.Key key) {
        this.conflictedKey = key;
    }

    private double fitX(double x) {
        return Range
                .between(0.0, x)
                .fit(width - x);
    }

    private double fitY(double y) {
        return Range
                .between(0.0, (double) (height - lineHeight * PolyproleneClient.configuration.maximumAutoSuggestions + 1))
                .fit(y);
    }

    private int getFittedX() {
        return (int) (fitX(baseX + offsetX));
    }

    private int getFittedY() {
        return (int) (fitY(baseY + offsetY));
    }

}
