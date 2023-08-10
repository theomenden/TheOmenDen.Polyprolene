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
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.apache.commons.compress.utils.Lists;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.mixin.KeyBindAccessorMixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyComponent extends PressableWidget implements TickableWidget {
    private final InputUtil.Key key;
    public float xCoord;
    public float yCoord;
    public PolyproleneKeyboardScreen polyproleneKeyboardScreen;
    protected float width;
    protected float height;
    private List<Text> tooltipText = Lists.newArrayList();

    public KeyComponent(int keyCode, float x, float y, float width, float height, InputUtil.Type keyType) {
        super((int) x, (int) y, (int) width, (int) height, Text.of(""));
        this.xCoord = x;
        this.yCoord = y;
        this.width = width;
        this.height = height;
        this.key = keyType.createFromCode(keyCode);
        this.setMessage(this.key.getLocalizedText());
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
        context.drawTextWithShadow(textRenderer, (OrderedText) this.getMessage(),
                (int) (this.xCoord + (this.width / 2) - textRenderer.getWidth(this.getMessage()) / 2),
                (int) (this.yCoord + (this.height - 6) / 2),
                color);
    }

    private int getColor() {
        int bindingCount = this.tooltipText.size();

        int color = 0;

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
        Arrays
                .stream(MinecraftClient.getInstance().options.allKeys)
                .filter(kb -> ((KeyBindAccessorMixin) kb)
                        .getBoundKey()
                        .equals(this.key))
                .forEach(kb -> tooltipText.add(Text.translatable(kb.getTranslationKey())));

        this.tooltipText = tooltipText
                .stream()
                .sorted()
                .map(s -> Text.translatable(s.getString()))
                .collect(Collectors.toCollection(ArrayList<Text>::new));
    }
}
