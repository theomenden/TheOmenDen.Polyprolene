package theomenden.polyprolene.client;

import me.shedaniel.clothconfig2.api.TickableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
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
import theomenden.polyprolene.components.KeyboardComponent;
import theomenden.polyprolene.utils.KeyInfoUtils;

import java.util.Arrays;

public class PolyproleneKeyboardScreen extends GameOptionsScreen {
    private int mouseCodeIndex = 0;
    private KeyboardComponent keyboard;
    private KeyboardComponent mouseButtons;
    private Key
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
        Arrays
                .stream(this.client.options.allKeys)
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
        this.bindingList = new KeyBindingListWidget(this, 10, 10, bindingListWidth, this.height - 40, this.textRenderer.fontHeight * 3 + 10);
        this.keyboard = KeyboardWidgetBuilder.standardKeyboard(this, bindingListWidth + 15, this.height / 2 - 90, this.width - (bindingListWidth + 15), 180);
        this.categorySelector = new CategorySelectorWidget(this, bindingListWidth + 15, 5, maxCategoryWidth[0] + 20, 20);
        this.screenToggleButton = new TexturedButtonWidget(this.width - 22, this.height - 22, 20, 20, 20, 0, 20, KeyWizard.SCREEN_TOGGLE_WIDGETS, 40, 40, (btn) -> {
            this.client.openScreen(new ControlsOptionsScreen(this.parent, this.gameOptions));
        });
        this.searchBar = new TextFieldWidget(this.textRenderer, 10, this.height - 20, bindingListWidth, 14, Text.of(""));
        this.mouseButton = KeyboardWidgetBuilder.singleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, mouseCodes[mouseCodeIndex], InputUtil.Type.MOUSE);
        this.mousePlus = new ButtonWidget((int) this.mouseButton.getAnchorX() + 83, (int) this.mouseButton.getAnchorY(), 25, 20, Text.of("+"), (btn) -> {
            this.mouseCodeIndex++;
            if (this.mouseCodeIndex >= this.mouseCodes.length) {
                this.mouseCodeIndex = 0;
            }
            this.children.remove(this.mouseButton);
            this.mouseButton = KeyboardWidgetBuilder.singleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, mouseCodes[mouseCodeIndex], InputUtil.Type.MOUSE);
            this.children.add(this.mouseButton);
        });
        this.mouseMinus = new ButtonWidget((int) this.mouseButton.getAnchorX() - 26, (int) this.mouseButton.getAnchorY(), 25, 20, Text.of("-"), (btn) -> {
            this.mouseCodeIndex--;
            if (this.mouseCodeIndex < 0) {
                this.mouseCodeIndex = this.mouseCodes.length - 1;
            }
            this.children.remove(this.mouseButton);
            this.mouseButton = KeyboardWidgetBuilder.singleKeyKeyboard(this, mouseButtonX, mouseButtonY, mouseButtonWidth, mouseButtonHeight, mouseCodes[mouseCodeIndex], InputUtil.Type.MOUSE);
            this.children.add(this.mouseButton);
        });
        this.resetBinding = new ButtonWidget(bindingListWidth + 15, this.height - 23, 50, 20, new TranslatableText("controls.reset"), (btn) -> {
            KeyBinding selectedBinding = this.getSelectedKeyBinding();
            selectedBinding.setBoundKey(selectedBinding.getDefaultKey());
            KeyBinding.updateKeysByCode();
        });
        this.clearBinding = new ButtonWidget(bindingListWidth + 66, this.height - 23, 50, 20, new TranslatableText("gui.clear"), (btn) -> {
            KeyBinding selectedBinding = this.getSelectedKeyBinding();
            selectedBinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN));
            KeyBinding.updateKeysByCode();
        });
        this.resetAll = new ButtonWidget(bindingListWidth + 117, this.height - 23, 70, 20, new TranslatableText("controls.resetAll"), (btn) -> {
            for (KeyBinding b : this.gameOptions.keysAll) {
                b.setBoundKey(b.getDefaultKey());
            }
            KeyBinding.updateKeysByCode();
        });

        this.addDrawableChild(this.);
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
                .filter(e -> e instanceof TickableWidget)
                .forEach(e -> ((TickableWidget) e).tick());
    }

    @Override
    public void render(DrawContext context, OptionListWidget optionButtons, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(context);
        this
                .children()
                .stream()
                .filter(b -> b instanceof Drawable)
                .forEach(b -> ((Drawable) b).render(context, mouseX, mouseY, tickDelta));
    }

    @Nullable
    public KeyBinding getSelectedKeyBind() {
        return this.bindingList.getSelectedKeyBinding();
    }

    public boolean getExtendedCategorySelector() {
        return this.categorySelector.extended;
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
