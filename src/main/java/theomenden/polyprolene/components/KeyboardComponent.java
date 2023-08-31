package theomenden.polyprolene.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.interfaces.ITickableElement;

import java.util.List;
import java.util.Map;

public final class KeyboardComponent extends AbstractParentElement implements Drawable, ITickableElement, Selectable {
    private final Map<Integer, KeyComponent> keyboardComponentMap = Maps.newHashMap();
    @Getter
    private final float anchorX;
    @Getter
    private final float anchorY;
    public PolyproleneKeyboardScreen polyproleneKeyboardScreen;

    KeyboardComponent(PolyproleneKeyboardScreen keyboardScreen, float anchorX, float anchorY) {
        this.polyproleneKeyboardScreen = keyboardScreen;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    @Override
    public void tick() {
        this
                .children()
                .forEach(KeyComponent::tick);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<? extends KeyComponent> keys = this.children();

        keys.forEach(key -> key.render(context, mouseX, mouseY, delta));

        if (!polyproleneKeyboardScreen.getExtendedCategorySelector()) {
            for (KeyComponent key : keys) {
                if (key.active && key.isHovered()) {
                    var positioner = key
                            .getTooltipText()
                            .stream()
                            .map(
                                    Text::asOrderedText
                            )
                            .toList();
                    this.polyproleneKeyboardScreen
                            .setTooltip(positioner);
                }
            }
        }

    }

    @Override
    public List<? extends KeyComponent> children() {
        return Lists.newArrayList(this.keyboardComponentMap
                .values()
                .iterator());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (polyproleneKeyboardScreen.getExtendedCategorySelector()) {
            return false;
        }

        return this
                .children()
                .stream()
                .anyMatch(
                        k -> k.mouseClicked(mouseX, mouseY, button)
                );
    }


    public float addKeyToMap(float relativeX, float relativeY, float width, float height, float keySpacing, int keyCode) {
        this.keyboardComponentMap
                .put(keyCode, new KeyComponent(polyproleneKeyboardScreen, keyCode, this.anchorX + relativeX, this.anchorY + relativeY, width, height, InputUtil.Type.KEYSYM));

        return relativeX + width + keySpacing;
    }

    public float addKeyToMap(float relativeX, float relativeY, float width, float height, float keySpacing, int keyCode, InputUtil.Type keyType) {
        this.keyboardComponentMap.put(keyCode, new KeyComponent(polyproleneKeyboardScreen, keyCode, this.anchorX + relativeX, this.anchorY + relativeY, width, height, keyType));
        return relativeX + width + keySpacing;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Narration.EMPTY);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
