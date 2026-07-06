package interfaces;

/**
 * Interface representing "the ability to report completion state".
 *
 * OOP CONCEPT - ABSTRACTION via INTERFACE:
 *   A pure contract - no fields, no method bodies. Any class that
 *   "implements Completable" promises the compiler it will provide real
 *   code for isCompleted().
 *
 * Why an interface here instead of just a method directly on Task?
 *   Right now only Task implements this, but the contract is deliberately
 *   separated so that OTHER unrelated types (e.g. a future Milestone or
 *   Subtask class) could also be treated as "completable" by any code that
 *   works against this interface, without needing to share a common parent
 *   class. This is the same reasoning as Transactable in the banking lab.
 */
public interface Completable {

    /**
     * @return true if this item is finished/done, false otherwise.
     */
    boolean isCompleted();
}
