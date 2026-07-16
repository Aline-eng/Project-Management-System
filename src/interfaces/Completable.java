package interfaces;

/**
 * Contract for anything that can report whether it is finished. Kept
 * separate from any class hierarchy so unrelated types could implement
 * it in the future without joining Task's parent chain.
 */
public interface Completable {

    boolean isCompleted();
}
