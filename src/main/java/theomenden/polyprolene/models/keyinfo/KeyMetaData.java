package theomenden.polyprolene.models.keyinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@AllArgsConstructor
@Accessors(fluent = true)
public class KeyMetaData {
    protected String layoutAuthor;
    protected String layoutName;
    protected String languageCode;
}
