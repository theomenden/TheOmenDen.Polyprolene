package theomenden.polyprolene.models.keyinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@ToString
@Accessors(fluent = true)
public class KeyData {
    protected int keyCode;
    protected String name;
    protected String translationKey;
    protected boolean isEnabled;
    @Setter
    protected boolean isMouseKey;
    protected int xPadding;
    protected int yPadding;
}
