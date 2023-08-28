package theomenden.polyprolene.utils;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import theomenden.polyprolene.models.records.ButtonWidgetConstruct;

public final class ButtonWidgetUtils {
    public static ButtonWidget buildSymbolButton(ButtonWidgetConstruct construct) {
        return ButtonWidget
                .builder(Text.of(construct
                        .Base()
                        .ButtonText()), construct
                        .Base()
                        .OnPress())
                .dimensions(construct
                                .Dimensions()
                                .X(),
                        construct
                                .Dimensions()
                                .Y(),
                        construct
                                .Dimensions()
                                .Width(),
                        construct
                                .Dimensions()
                                .Height())
                .build();
    }

    public static ButtonWidget buildButton(ButtonWidgetConstruct construct) {
        return ButtonWidget
                .builder(Text.translatable(construct
                        .Base()
                        .ButtonText()), construct
                        .Base()
                        .OnPress())
                .dimensions(construct
                        .Dimensions()
                        .X(), construct
                        .Dimensions()
                        .Y(), construct
                        .Dimensions()
                        .Width(), construct
                        .Dimensions()
                        .Height())
                .build();
    }
}
