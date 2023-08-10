package theomenden.polyprolene.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.TickableWidget;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.InputUtil;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;

import java.util.List;
import java.util.Map;

public class KeyboardComponent extends AbstractParentElement implements Drawable, TickableWidget {
    private final Map<Integer, KeyComponent> keyboardComponentMap = Maps.newHashMap();
    private final float anchorX;
    private final float anchorY;
    public PolyproleneKeyboardScreen polyproleneKeyboardScreen;

    protected KeyboardComponent(PolyproleneKeyboardScreen keyboardScreen, float anchorX, float anchorY) {
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

        keys
                .forEach(k -> k.render(context, mouseX, mouseY, delta));

        if (!polyproleneKeyboardScreen.getExtendedCategorySelector()) {
            keys
                    .stream()
                    .filter(k -> k.active && k.isHovered())
                    .forEach(k -> polyproleneKeyboardScreen
                            .renderWithTooltip(context, mouseX, mouseY, delta));
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
                .put(keyCode, new KeyComponent(keyCode, this.anchorX + relativeX, this.anchorY + relativeY, width, height, InputUtil.Type.KEYSYM));

        return relativeX + width + keySpacing;
    }

    public float addKeyToMap(float relativeX, float relativeY, float width, float height, float keySpacing, int keyCode, InputUtil.Type keyType) {
        this.keyboardComponentMap.put(keyCode, new KeyComponent(keyCode, this.anchorX + relativeX, this.anchorY + relativeY, width, height, keyType));
        return relativeX + width + keySpacing;
    }

    public float getAnchorX() {
        return this.anchorX;
    }

    public float getAnchorY() {
        return this.anchorY;
    }
}
