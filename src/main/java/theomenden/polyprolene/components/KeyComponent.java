package theomenden.polyprolene.components;

import me.shedaniel.clothconfig2.api.TickableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.apache.commons.compress.utils.Lists;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.mixin.keys.KeyBindAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class KeyComponent extends PressableWidget implements TickableWidget {
    private final InputUtil.Key key;
    private final PolyproleneKeyboardScreen polyproleneKeyboardScreen;
    private final float xCoord;
    private final float yCoord;
    private final float width;
    private final float height;
    private List<Text> tooltipText = Lists.newArrayList();

    public KeyComponent(PolyproleneKeyboardScreen screen, int keyCode, float x, float y, float width, float height, InputUtil.Type keyType) {
        super((int) x, (int) y, (int) width, (int) height, Text.of(""));
        this.xCoord = x;
        this.yCoord = y;
        this.width = width;
        this.height = height;
        this.key = keyType.createFromCode(keyCode);
        this.setMessage(this.key.getLocalizedText());
        this.polyproleneKeyboardScreen = screen;
    }

    @Override
    public void tick() {
        updateTooltip();
    }

    @Override
    public void onPress() {
        this.playDownSound(MinecraftClient
                .getInstance()
                .getSoundManager());

        if (Screen.hasShiftDown()) {
            Text t = this.getMessage();
            String keyName;

            if (t instanceof TranslatableTextContent translatableTextContent) {
                keyName = I18n.translate(translatableTextContent.getKey());
            } else {
                keyName = t.getString();
            }

            polyproleneKeyboardScreen.setSearchBarText("<" + keyName + ">");
        } else {
            KeyBinding selectedKeyBinding = polyproleneKeyboardScreen.getSelectedKeyBind();

            if (selectedKeyBinding != null) {
                selectedKeyBinding.setBoundKey(this.key);
                KeyBinding.updateKeysByCode();
            }
        }
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = getColor();

        context.drawBorder((int) xCoord,
                (int) yCoord,
                (int) (xCoord + width),
                (int) (yCoord + height),
                color);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawTextWithShadow(
                textRenderer,
                this.getMessage(),
                (int) (this.xCoord + (this.width / 2) - textRenderer.getWidth(this.getMessage()) / 2),
                (int) (this.yCoord + (this.height - 6) / 2),
                color);
    }

    private int getColor() {
        int bindingCount = this.tooltipText.size();

        int color;

        if (this.active) {

            if (this.isHovered()
                    && !polyproleneKeyboardScreen.getExtendedCategorySelector()) {
                color = 0xFFAAAAAA;

                if (bindingCount == 1) {
                    color = 0xFF00AA00;
                } else if (bindingCount > 1) {
                    color = 0xFFAA0000;
                }
            } else {
                color = 0xFFFFFFFF;
                if (bindingCount == 1) {
                    color = 0xFF00FF00;
                } else if (bindingCount > 1) {
                    color = 0xFFFF0000;
                }
            }

        } else {
            color = 0xFF555555;
        }
        return color;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.HINT, this.getNarrationMessage());
        builder.nextMessage();
    }

    private void updateTooltip() {
        ArrayList<String> tooltipText = Arrays
                .stream(MinecraftClient.getInstance().options.allKeys)
                .filter(b -> ((KeyBindAccessor) b)
                        .getBoundKey()
                        .equals(this.key))
                .map(b -> I18n.translate(b.getTranslationKey()))
                .collect(Collectors.toCollection(ArrayList::new));

        this.tooltipText = tooltipText
                .stream()
                .sorted()
                .map(s -> MutableText.of(new TranslatableTextContent(s, this.key.getTranslationKey(), null)))
                .collect(Collectors.toCollection(ArrayList<Text>::new));
    }
}
