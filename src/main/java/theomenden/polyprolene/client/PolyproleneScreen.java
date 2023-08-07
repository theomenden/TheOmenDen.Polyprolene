package theomenden.polyprolene.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.manager.KeyBindingsManager;
import theomenden.polyprolene.mixin.KeyBindAccessorMixin;
import theomenden.polyprolene.models.AutoCompleteResult;
import theomenden.polyprolene.models.KeyBindSuggestion;
import theomenden.polyprolene.utils.MathUtils;

import java.util.List;
import java.util.stream.IntStream;

public class PolyproleneScreen extends Screen {
    private final int SUGGESTION_COLOR = 0x999999;
    private final int HIGHLIGHT_COLOR = 0xFFFF00;

    public static double offsetX = 0;
    public static double offsetY = 0;

    private int baseX;
    private int baseY;

    private TextFieldWidget textField;
    private AutoCompleteResult autoCompleteResult;
    private int selected = 0;
    private int optionsOffset = 0;
    public int configurableWidth = 250;
    public int lineHeight = 12;
    int timeIn = 0;
    int slotSelected = -1;

    private InputUtil.Key conflictedKeyBinding = InputUtil.UNKNOWN_KEY;

    final MinecraftClient minecraftClient;

    public PolyproleneScreen() {
        super(NarratorManager.EMPTY);
        autoCompleteResult = new AutoCompleteResult();
        autoCompleteResult.updateSuggestionsList("");
        offsetX = PolyproleneClient.configuration.launcherX;
        offsetY = PolyproleneClient.configuration.launcherY;
        minecraftClient = MinecraftClient.getInstance();
    }

    @Override
    protected void init() {
        baseX = (width - configurableWidth) / 2;
        baseY = (height - lineHeight) / 2;
        String text = "";

        if(textField != null) {
            text = textField.getText();
        }

        textField = new TextFieldWidget(textRenderer, getX(), getY() + 1, configurableWidth, lineHeight, NarratorManager.EMPTY);
        textField.setDrawsBackground(false);
        textField.setChangedListener(this::textChangeListener);
        textField.setText(text);
        addSelectableChild(textField);
        setInitialFocus(textField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<KeyBindSuggestion> suggestions = autoCompleteResult.getCurrentSuggestions();
        int lineAmt = Math.min(suggestions.size(), PolyproleneClient.configuration.maximumAutoSuggestions);
        int bgColor = (int) Math.round(PolyproleneClient.configuration.backgroundOpacity * 255) * 0x1000000;
        context.fill(getX()-1, getY()-1, getX()+configurableWidth-1, getY()+lineHeight-2 + lineAmt * lineHeight, bgColor);
        renderBackground(context);
        textField.setFocused(true);
        textField.render(context, mouseX, mouseY, delta);

        int y = getY();

        for (int i = optionsOffset; i - optionsOffset < PolyproleneClient.configuration.maximumAutoSuggestions; i++) {
            if (suggestions.size() <= i) break;
            KeyBindSuggestion suggestionAtIndex = suggestions.get(i);

            y += lineHeight;

            if (suggestionAtIndex.isAFavorite) {
                context.fill(getX()-3, y-2, getX()-1, y+lineHeight-2, HIGHLIGHT_COLOR | bgColor);
            }

            // draw the bind name
            int contestedColor = i == selected ? HIGHLIGHT_COLOR : SUGGESTION_COLOR;
            context.drawTextWithShadow(textRenderer, suggestionAtIndex.name, getX(), y, contestedColor);

            // draw the bind category
            int textWidth = textRenderer.getWidth(suggestionAtIndex.category) + 2;
            context.drawTextWithShadow(textRenderer, suggestionAtIndex.category, getX() + configurableWidth - textWidth, y, SUGGESTION_COLOR);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        textField.tick();

        if (!InputUtil.isKeyPressed(MinecraftClient
                .getInstance()
                .getWindow()
                .getHandle(), conflictedKeyBinding.getCode())) {
            minecraftClient.setScreen(null);
            if (slotSelected != -1) {
                KeyBinding bind = KeyBindingsManager.getConflictingBindingsForKey(conflictedKeyBinding).get(slotSelected);
                ((KeyBindAccessorMixin) bind).setPressed(true);
                ((KeyBindAccessorMixin) bind).setTimesPressed(1);
            }
        }
        timeIn++;
    }

    @Override
    public void removed() {
        PolyproleneClient.configuration.launcherX = offsetX;
        PolyproleneClient.configuration.launcherY = offsetY;
        AutoConfig.getConfigHolder(PolyproleneConfig.class).save();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(PolyproleneClient.favoriteKey.matchesKey(keyCode, scanCode)) {
            List<KeyBindSuggestion> suggestions = autoCompleteResult.getCurrentSuggestions();

            if(suggestions.size() > selected) {
                AutoCompleteResult.toggleFavorite(suggestions.get(selected));
                return true;
            }
        }

        if(keyCode == GLFW.GLFW_KEY_UP) {
            switchBetweenSelection(-1);
            return true;
        }

        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            switchBetweenSelection(1);
            return true;
        }


        if(keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            List<KeyBindSuggestion> suggestions = autoCompleteResult.getCurrentSuggestions();
            close();

            if(suggestions.size() > selected) {
                suggestions.get(selected).execute();
            }

            return true;
        }

        return textField.keyPressed(keyCode, scanCode, modifiers)
                || super.keyPressed(keyCode,scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button != 1) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        offsetX = 0;
        offsetY = 0;
        init();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        offsetX = fitX(offsetX + deltaX + baseX) - baseX;
        offsetY = fitX(offsetY + deltaX + baseY) - baseY;
        init();
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var offsetAbsolute = Math.abs(offsetX) + Math.abs(offsetY);
        if(offsetAbsolute >= 15) {
            return false;
        }

        offsetX = 0;
        offsetY = 0;
        init();
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        init(client, width, height);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void switchBetweenSelection(int by) {
        int totalSuggestions = autoCompleteResult.getCurrentSuggestions().size();

        if(totalSuggestions == 0) {
            selected = 0;
            return;
        }

        selected = (selected + by + totalSuggestions) % totalSuggestions;

        if(optionsOffset > selected) {
            optionsOffset = selected;
            return;
        }

        if(optionsOffset + PolyproleneClient.configuration.maximumAutoSuggestions <= selected) {
            optionsOffset = selected - PolyproleneClient.configuration.maximumAutoSuggestions;
        }
    }

    public void textChangeListener(String s) {
        autoCompleteResult.updateSuggestionsList(s);
        selected = 0;
        optionsOffset = 0;
    }

    public void setConflictedKeyBinding(InputUtil.Key key) {
        this.conflictedKeyBinding = key;
    }

    private static void fillBufferBuilder(int segment, float degreesPerSegment, int xCoord, float radius, int yCoord, BufferBuilder bufferBuilder, int r, int g, int b, int a) {
        var incremental = degreesPerSegment + MathUtils.RADIANS_TO_DEGREES * 0.5;

        for (float i = 0; i < incremental; i += (float) MathUtils.RADIANS_TO_DEGREES) {
            float rad = i + segment * degreesPerSegment;
            float xp = xCoord + MathHelper.cos(rad) * radius;
            float yp = yCoord + MathHelper.sin(rad) * radius;

            if (i == 0) {
                bufferBuilder
                        .vertex(xp, yp, 0).color(r, g, b, a).next();
            }

            bufferBuilder
                    .vertex(xp, yp, 0).color(r, g, b, a).next();
        }
    }

    private void createKeyPressedEvent() {
        if(slotSelected == -1) {
            return;
        }

        KeyBinding keyBinding = KeyBindingsManager.getConflictingBindingsForKey(conflictedKeyBinding).get(slotSelected);
        ((KeyBindAccessorMixin) keyBinding).setPressed(true);
        ((KeyBindAccessorMixin) keyBinding).setTimesPressed(1);
    }

    private static double calculateMouseAngle(int x, int y, int mouseX, int mouseY) {
        double deltaX = mouseX - x;
        double deltaY = mouseY - y;
        return Math.atan2(deltaY,deltaX);
    }

    @NotNull
    private static String getSectorName(int segment, float degreesPerSegment, double mouseAngleAt, String boundKey) {
        return (isMouseInSector(degreesPerSegment, mouseAngleAt, segment)
                ? Formatting.UNDERLINE
                : Formatting.RESET) + boundKey;
    }

    private float calculateMaximumRadius(float delta, int segment, float conflictingSegmentInv, int maxRadius) {
        return Math.max(0F, Math.min(((timeIn + delta) - (segment * 6F * conflictingSegmentInv)) * 40F, maxRadius));
    }

    private static boolean isMouseInSector(float degreesPerSegment, double mouseAngleAt, int segment) {
        return degreesPerSegment * segment < mouseAngleAt && mouseAngleAt < mouseAngleAt * (segment + 1);
    }

    private double fitX(double x){
        return Range.between(0.0, x).fit((double)(width - configurableWidth));
    }

    private double fixY(double y) {
        return Range.between(0.0, y).fit((double)(lineHeight - PolyproleneClient.configuration.maximumAutoSuggestions + 1));
    }

    private int getX() {
        return (int)(fitX(baseX + offsetX));
    }

    private int getY() {
        return (int)(fixY(baseY + offsetY));
    }
}
