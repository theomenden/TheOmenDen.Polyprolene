package theomenden.polyprolene.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;

import java.util.Objects;
import java.util.stream.IntStream;

public abstract class FreeFormListComponent<T extends FreeFormListComponent<T>.Entry> extends EntryListWidget<FreeFormListComponent<T>.Entry> {

    public boolean isVisible = true;

    public FreeFormListComponent(MinecraftClient client, int width, int height, int top, int left, int itemHeight) {
        super(client, 0, 0, 0, 0, itemHeight);
        this.top = top;
        this.left = left;
        this.height = height;
        this.width = width;
        this.right = left + width;

        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected void renderBackground(DrawContext context) {
        context.fillGradient(this.left, this.top, this.right, this.bottom, -1072689136, -804253680);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isVisible) {
            return;
        }

        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.left + this.width - 5;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isVisible && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isVisible && super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.isVisible && super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.isVisible && super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isVisible && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        double scaleH = this.client
                .getWindow()
                .getHeight() / (double) this.client
                .getWindow()
                .getScaledHeight();
        double scaleW = this.client
                .getWindow()
                .getWidth() / (double) this.client
                .getWindow()
                .getScaledWidth();
        RenderSystem.enableScissor((int) (this.left * scaleW), (int) (this.client
                .getWindow()
                .getHeight() - (this.bottom * scaleH)), (int) (this.width * scaleW), (int) (this.height * scaleH));

        IntStream
                .range(0, this.getEntryCount())
                .forEach(i -> {
                    if (this.isSelectedEntry(i)) {
                        context.drawBorder(this.getRowLeft() - 2, this.getRowTop(i) - 2, this.getRowRight() - 8, this.getRowTop(i) + this.itemHeight - 4, 0xFFFFFFFF);
                    }

                    var entry = this.getEntry(i);
                    var mouseIsOver = this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), entry);

                    entry.render(context, i, this.getRowTop(i), this.getRowLeft(), this.width, this.itemHeight - 4, mouseX, mouseY, mouseIsOver, delta);
                    RenderSystem.disableScissor();
                });

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.isVisible && super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.isVisible && super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.isVisible && super.charTyped(chr, modifiers);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    public abstract class Entry extends EntryListWidget.Entry<FreeFormListComponent<T>.Entry> {
        @Override
        public abstract void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.onPressed();
                return true;
            }

            return false;
        }

        private void onPressed() {
            FreeFormListComponent.this.setSelected(this);
        }
    }
}
