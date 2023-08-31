package theomenden.polyprolene.components;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import theomenden.polyprolene.client.PolyproleneKeyboardScreen;
import theomenden.polyprolene.interfaces.ITickableElement;
import theomenden.polyprolene.utils.KeyInfoUtils;

public final class CategoryComponent extends PressableWidget implements ITickableElement {
    private final PolyproleneKeyboardScreen polyproleneKeyboardScreen;
    @Getter
    private final BindingCategoryListComponent categoryList;
    public boolean isExtended = false;

    public CategoryComponent(PolyproleneKeyboardScreen screen, int x, int y, int width, int height) {
        super(x, y, width, height, Text.of(""));
        this.polyproleneKeyboardScreen = screen;
        MinecraftClient client = MinecraftClient.getInstance();

        int listItemHeight = client.textRenderer.fontHeight + 7;
        int listHeight = KeyInfoUtils
                .getAppendedDynamicCategories()
                .size() * listItemHeight + 10;
        int listBottom = this.getY() + this.height + listHeight;

        if (listBottom > this.polyproleneKeyboardScreen.height) {
            listHeight = this.polyproleneKeyboardScreen.height - this.getY() - this.height - 10;
        }

        this.categoryList = new BindingCategoryListComponent(client, this.getY() + this.height, this.getX(), this.width, listHeight, listItemHeight);
    }

    @Override
    public void tick() {
        this.setMessage(Text.translatable(this.getSelectedCategory()));
        this.categoryList.isVisible = this.isExtended;
    }

    @Override
    public void onPress() {
        this.playDownSound(MinecraftClient
                .getInstance()
                .getSoundManager());

        this.isExtended = !this.isExtended;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.categoryList.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isListClicked = this.categoryList.mouseClicked(mouseX, mouseY, button);
        boolean hasThisBeenClicked = super.mouseClicked(mouseX, mouseY, button);

        final boolean wasListOrThisClicked = isListClicked || hasThisBeenClicked;

        if (!(wasListOrThisClicked)) {
            this.isExtended = false;
        }
        return wasListOrThisClicked;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public String getSelectedCategory() {
        if (this.categoryList.getSelectedOrNull() == null) {
            return KeyInfoUtils.DYNAMIC_CATEGORIES;
        }
        return ((BindingCategoryListComponent.CategoryEntry) this.categoryList.getSelectedOrNull()).category;
    }

    public static final class BindingCategoryListComponent extends FreeFormListComponent<BindingCategoryListComponent.CategoryEntry> {

        public BindingCategoryListComponent(MinecraftClient client, int top, int left, int width, int height, int itemHeight) {
            super(client, width, height, top, left, itemHeight);

            KeyInfoUtils
                    .getAppendedDynamicCategories()
                    .stream()
                    .map(CategoryEntry::new)
                    .forEach(this::addEntry);
            this.setSelected(this
                    .children()
                    .get(0));
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
            builder.put(NarrationPart.TITLE, Narration.text(Text.translatable("key.polyprolene.categories.all")));
        }

        public final class CategoryEntry extends FreeFormListComponent<BindingCategoryListComponent.CategoryEntry>.Entry {
            private final String category;

            public CategoryEntry(String category) {
                this.category = category;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawTextWithShadow(client.textRenderer, Text.of(this.category), x + 3, y + 2, 0xFFFFFFFF);
            }

        }
    }
}
