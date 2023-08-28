package theomenden.polyprolene.models.keyinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Accessors(fluent = true)
public class KeyDataRow {
    protected List<KeyData> row;
}
