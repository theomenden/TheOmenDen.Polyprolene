package theomenden.polyprolene.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@EqualsAndHashCode
@Accessors(fluent = true, chain = true)
public class Point<T> {

    @Setter
    private T xCoord;
    @Getter
    @Setter
    private T yCoord;

    public Point(T x, T y) {
        this.xCoord = x;
        this.yCoord = y;
    }

    public void setCoordinates(T x, T y) {
        this.xCoord = x;
        this.yCoord = y;
    }

}
