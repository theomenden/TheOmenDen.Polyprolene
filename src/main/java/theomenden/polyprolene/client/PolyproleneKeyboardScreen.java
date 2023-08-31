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
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import theomenden.polyprolene.components.CategoryComponent;
import theomenden.polyprolene.components.KeyBindingListComponent;
import theomenden.polyprolene.components.KeyboardComponent;
import theomenden.polyprolene.components.KeyboardComponentBuilder;
import theomenden.polyprolene.enums.MouseButtons;
import theomenden.polyprolene.interfaces.ITickableElement;
import theomenden.polyprolene.models.records.ButtonDimensions;
import theomenden.polyprolene.models.records.ButtonWidgetBase;
import theomenden.polyprolene.models.records.ButtonWidgetConstruct;
import theomenden.polyprolene.utils.ButtonWidgetUtils;
import theomenden.polyprolene.utils.KeyInfoUtils;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class PolyproleneKeyboardScreen extends GameOptionsScreen {
    private int mouseCodeIndex = 0;
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

        final int[] maxBindingNameWidth = {0};
        final KeyBinding[] allKeys = this.client.options.allKeys;
        Arrays
                .stream(allKeys)
                .forEach(k -> {
                    int w = this.textRenderer.getWidth(Text.translatable(k.getTranslationKey()));
                    if (w > maxBindingNameWidth[0]) {
                        maxBindingNameWidth[0] = w;
                    }
                });

        final int[] maxCategoryWidth = {0};
        KeyInfoUtils
                .getCategories()
                .forEach(k -> {
                    int w = this.textRenderer.getWidth(Text.translatable(k));
                    if (w > maxCategoryWidth[0]) {
                        maxCategoryWidth[0] = w;
                    }
                });


        int bindingListWidth = (maxBindingNameWidth[0] + 20);
        this.bindingList = new KeyBindingListComponent(this, 10, 10, bindingListWidth, this.height - 40, this.textRenderer.fontHeight * 3 + 10);
        this.keyboard = KeyboardComponentBuilder.buildStandardKeyBoard(this, bindingListWidth + 15, (float) this.height / 2f - 90, this.width - (bindingListWidth + 15), 180);
        this.categorySelector = new CategoryComponent(this, bindingListWidth + 15, 5, maxCategoryWidth[0] + 20, 20);
        this.screenToggler = new TexturedButtonWidget(this.width - 22, this.height - 22, 20, 20, 20, 0, 20, PolyproleneClient.SCREEN_WIDGETS, 40, 40, (btn) -> this.client.setScreen(new ControlsOptionsScreen(this.parent, this.gameOptions)));
        this.searchBar = new TextFieldWidget(this.textRenderer, 10, this.height - 20, bindingListWidth, 14, Text.of(""));

        this.mouseButtons = KeyboardComponentBuilder.buildSingleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, MouseButtons.MOUSE_BUTTONS[mouseCodeIndex], InputUtil.Type.MOUSE);

        this.mousePlus = ButtonWidgetUtils.buildSymbolButton(
                new ButtonWidgetConstruct(
                        new ButtonWidgetBase("+", btn -> {
                            this.mouseCodeIndex++;
                            if (this.mouseCodeIndex >= MouseButtons.MOUSE_BUTTONS.length) {
                                this.mouseCodeIndex = 0;
                            }
                            this
                                    .children()
                                    .remove(this.mouseButtons);
                            this.mouseButtons = KeyboardComponentBuilder.buildSingleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, MouseButtons.MOUSE_BUTTONS[mouseCodeIndex].ordinal(), InputUtil.Type.MOUSE);
                            this.addDrawableChild(this.mouseButtons);
                        }),
                        new ButtonDimensions((int) this.mouseButtons.getAnchorX() + 83, (int) this.mouseButtons.getAnchorY(), 25, 20)));

        this.mouseMinus = ButtonWidgetUtils.buildSymbolButton(
                new ButtonWidgetConstruct(
                        new ButtonWidgetBase("-", btn -> {
                            this.mouseCodeIndex--;
                            if (this.mouseCodeIndex < 0) {
                                this.mouseCodeIndex = MouseButtons.MOUSE_BUTTONS.length - 1;
                            }
                            this
                                    .children()
                                    .remove(this.mouseButtons);
                            this.mouseButtons = KeyboardComponentBuilder.buildSingleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, MouseButtons.MOUSE_BUTTONS[mouseCodeIndex].ordinal(), InputUtil.Type.MOUSE);
                            this.addDrawableChild(this.mouseButtons);
                        }),
                        new ButtonDimensions((int) this.mouseButtons.getAnchorX() - 26, (int) this.mouseButtons.getAnchorY(), 25, 20)
                )
        );

        this.resetButton = ButtonWidgetUtils.buildButton(
                new ButtonWidgetConstruct(
                        new ButtonWidgetBase("controls.reset", btn -> {
                            KeyBinding selectedBinding = this.getSelectedKeyBind();
                            selectedBinding.setBoundKey(selectedBinding.getDefaultKey());
                            KeyBinding.updateKeysByCode();
                        }),
                        new ButtonDimensions(bindingListWidth + 15, this.height - 23, 50, 20)
                )
        );

        this.clearBindingsButton = ButtonWidgetUtils.buildButton(
                new ButtonWidgetConstruct(
                        new ButtonWidgetBase("gui.clear", btn -> {
                            KeyBinding selectedBinding = this.getSelectedKeyBind();
                            selectedBinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN));
                            KeyBinding.updateKeysByCode();
                        }),
                        new ButtonDimensions(bindingListWidth + 66, this.height - 23, 50, 20)
                )
        );

        this.resetAllButton = ButtonWidgetUtils.buildButton(
                new ButtonWidgetConstruct(
                        new ButtonWidgetBase("controls.resetAll", btn -> {
                            Arrays
                                    .stream(this.gameOptions.allKeys)
                                    .forEach(b -> b.setBoundKey(b.getDefaultKey()));
                            KeyBinding.updateKeysByCode();
                        }),
                        new ButtonDimensions(bindingListWidth + 117, this.height - 23, 70, 20)
                )
        );

        this.addDrawableChild(this.bindingList);
        this.addDrawableChild(this.keyboard);
        this.addDrawableChild(this.categorySelector);
        this.addDrawableChild(this.categorySelector.getCategoryList());
        this.addDrawableChild(this.screenToggler);
        this.addDrawableChild(this.searchBar);
        this.addDrawableChild(this.mouseButtons);
        this.addDrawableChild(this.mousePlus);
        this.addDrawableChild(this.mouseMinus);
        this.addDrawableChild(this.resetButton);
        this.addDrawableChild(this.clearBindingsButton);
        this.addDrawableChild(this.resetAllButton);
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
