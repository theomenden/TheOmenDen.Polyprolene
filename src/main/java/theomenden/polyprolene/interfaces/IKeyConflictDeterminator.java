package theomenden.polyprolene.interfaces;

public interface IKeyConflictDeterminator {
    boolean isACurrentActivelyKeyBinding();
    boolean isAConflictWith(IKeyConflictDeterminator other);
}
