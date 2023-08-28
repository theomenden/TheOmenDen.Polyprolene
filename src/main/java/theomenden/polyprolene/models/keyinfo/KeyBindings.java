package theomenden.polyprolene.models.keyinfo;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Getter
@ToString
@Accessors(fluent = true)
public class KeyBindings {
    protected List<KeyDataRow> standardBoard = Lists.newArrayList();
    protected List<KeyDataRow> mouseButtons = Lists.newArrayList();
    protected List<KeyDataRow> modifierKeys = Lists.newArrayList();
    protected List<KeyDataRow> functionalKeys = Lists.newArrayList();
    protected List<KeyDataRow> additionalKeys = Lists.newArrayList();
    protected List<KeyDataRow> numpadKeys = Lists.newArrayList();
}
