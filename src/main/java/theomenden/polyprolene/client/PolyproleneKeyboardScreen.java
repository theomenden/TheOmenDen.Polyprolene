package theomenden.polyprolene.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.components.CategoryComponent;
import theomenden.polyprolene.components.KeyBindingListComponent;
import theomenden.polyprolene.components.KeyboardComponent;
import theomenden.polyprolene.components.KeyboardComponentBuilder;
import theomenden.polyprolene.interfaces.ITickableElement;
import theomenden.polyprolene.utils.KeyInfoUtils;

@Environment(EnvType.CLIENT)
public class PolyproleneKeyboardScreen extends GameOptionsScreen {
    private final int mouseCodeIndex = 0;
    private KeyboardComponent keyboard;
    private KeyboardComponent mouseButtons;
    private KeyBindingListComponent bindingList;
    private CategoryComponent categorySelector;
    private TexturedButtonWidget screenToggler;
    private TextFieldWidget searchBar;
    private ButtonWidget mousePlus;
    private ButtonWidget mouseMinus;
    private ButtonWidget resetButton;
    private ButtonWidget resetAllButton;
    private ButtonWidget clearBindingsButton;

    public PolyproleneKeyboardScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.of(PolyproleneClient.MODID));
    }

    @Override
    protected void init() {
        int mouseButtonX = this.width - 105;
        int mouseButtonY = this.height / 2 - 115;
        int mouseButtonWidth = 80;
        int mouseButtonHeight = 20;

        int maximumBindingNameWidth = 0;

        for (var key : this.client.options.allKeys) {
            int keyNameWidth = this.textRenderer.getWidth(MutableText.of(new TranslatableTextContent(key.getTranslationKey(), key.getBoundKeyTranslationKey(), null)));
            if (keyNameWidth > maximumBindingNameWidth) {
                maximumBindingNameWidth = keyNameWidth;
            }
        }

        int maximumCategoryWidth = 0;
        for (var category : KeyInfoUtils.getCategories()) {
            int categoryNameWidth = this.textRenderer.getWidth(MutableText.of(new TranslatableTextContent(category, category, null)));
            if (categoryNameWidth > maximumCategoryWidth) {
                maximumCategoryWidth = categoryNameWidth;
            }
        }

        int bindingListWidth = (maximumBindingNameWidth + 20);
        this.bindingList = new KeyBindingListComponent(this, 10, 10, bindingListWidth, this.height - 40, this.textRenderer.fontHeight * 3 + 10);
        this.keyboard = KeyboardComponentBuilder.buildStandardKeyBoard(this, bindingListWidth + 15, this.height / 2 - 90, this.width - (bindingListWidth + 15), 180);
        this.categorySelector = new CategoryComponent(this, bindingListWidth + 15, 5, maximumCategoryWidth + 20, 20);
        this.screenToggler = new TexturedButtonWidget(this.width - 22, this.height - 22, 20, 20, 20, 0, 20, PolyproleneClient.SCREEN_WIDGETS, 40, 40, (btn) -> {
            this.client.setScreen(new ControlsOptionsScreen(this.parent, this.gameOptions));
        });
        this.searchBar = new TextFieldWidget(this.textRenderer, 10, this.height - 20, bindingListWidth, 14, Text.of(""));
        this.mouseButtons = KeyboardComponentBuilder.buildSingleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, mouse[mouseCodeIndex], InputUtil.Type.MOUSE);
        this.mousePlus = new ButtonWidget((int) this.mouseButtons.getAnchorX() + 83, (int) this.mouseButtons.getAnchorY(), 25, 20, Text.of("+"), (btn) -> {
            this.mouseCodeIndex++;
            if (this.mouseCodeIndex >= this.mouseCodes.length) {
                this.mouseCodeIndex = 0;
            }
            this.remove(this.mouseButtons);
            this.mouseButtons = KeyboardComponentBuilder.buildSingleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, mouseCodes[mouseCodeIndex], InputUtil.Type.MOUSE);
            this.addDrawableChild(this.mouseButtons);
        });
        this.mouseMinus = new ButtonWidget((int) this.mouseButtons.getAnchorX() - 26, (int) this.mouseButtons.getAnchorY(), 25, 20, Text.of("-"), (btn) -> {
            this.mouseCodeIndex--;
            if (this.mouseCodeIndex < 0) {
                this.mouseCodeIndex = this.mouseButtons. - 1;
            }
            this.remove(this.mouseButtons);
            this.mouseButtons = KeyboardComponentBuilder.buildSingleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, mouseCodes[mouseCodeIndex], InputUtil.Type.MOUSE);
            this.addDrawableChild(this.mouseButtons);
        });
        this.resetBinding = new ButtonWidget(bindingListWidth + 15, this.height - 23, 50, 20, MutableText.of(new TranslatableTextContent("controls.reset")), (btn) -> {
            KeyBinding selectedBinding = this.getSelectedKeyBind();
            selectedBinding.setBoundKey(selectedBinding.getDefaultKey());
            KeyBinding.updateKeysByCode();
        });
        this.clearBinding = new ButtonWidget(bindingListWidth + 66, this.height - 23, 50, 20, MutableText.of(new TranslatableTextContent("gui.clear")), (btn) -> {
            KeyBinding selectedBinding = this.getSelectedKeyBind();
            selectedBinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN));
            KeyBinding.updateKeysByCode();
        });
        this.resetAll = new ButtonWidget(bindingListWidth + 117, this.height - 23, 70, 20, MutableText.of(new TranslatableTextContent("controls.resetAll")), (btn) -> {
            for (KeyBinding b : this.gameOptions.allKeys) {
                b.setBoundKey(b.getDefaultKey());
            }
            KeyBinding.updateKeysByCode();
        });

        this.addDrawableChild(this.bindingList);
        this.addDrawableChild(this.keyboard);
        this.addDrawableChild(this.categorySelector);
        this.addDrawableChild(this.categorySelector.getCategoryList());
        this.addDrawableChild(this.screenToggleButton);
        this.addDrawableChild(this.searchBar);
        this.addDrawableChild(this.mouseButton);
        this.addDrawableChild(this.mousePlus);
        this.addDrawableChild(this.mouseMinus);
        this.addDrawableChild(this.resetBinding);
        this.addDrawableChild(this.clearBinding);
        this.addDrawableChild(this.resetAll);
    }

    @Override
    public void tick() {
        this
                .children()
                .stream()
                .filter(e -> e instanceof ITickableElement)
                .forEach(e -> ((ITickableElement) e).tick());
    }

    @Override
    public void render(DrawContext context, OptionListWidget optionButtons, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, tickDelta);
    }

    @Nullable
    public KeyBinding getSelectedKeyBind() {
        return this.bindingList.getSelectedKeyBinding();
    }

    public boolean getExtendedCategorySelector() {
        return this.categorySelector.isExtended;
    }

    public String getSelectedCategory() {
        return this.categorySelector.getSelectedCategory();
    }

    public String getFilteredText() {
        return this.searchBar.getText();
    }

    public void setSearchBarText(String searchBarText) {
        this.searchBar.setText(searchBarText);
    }
}
