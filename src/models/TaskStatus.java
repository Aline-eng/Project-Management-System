package models;

/**
 * Enum representing the fixed, valid set of statuses a Task can have.
 *
 * WHY AN ENUM INSTEAD OF A STRING:
 *   If status were just a String, nothing stops someone from typing
 *   task.setStatus("Donee") or "in progress " (typo/extra space) - the
 *   compiler would happily accept it, and the bug would only surface at
 *   runtime (or never, silently corrupting reports). An enum is a CLOSED,
 *   compiler-checked set of constants: Java will not compile code that
 *   tries to assign anything other than PENDING, IN_PROGRESS, or COMPLETED
 *   to a TaskStatus variable. This directly satisfies the lab's requirement
 *   to "use enums or constants for valid statuses" and prevents invalid
 *   status values by construction rather than by manual validation alone.
 */
public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    // Each enum constant carries its own "display label" - what the console
    // UI should show - separate from the enum's internal Java identifier
    // (IN_PROGRESS can't contain a space, but "In Progress" can be displayed).
    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts free-text user input (e.g. typed at a console prompt) into
     * the matching enum constant, or returns null if it doesn't match any
     * valid status. Callers are expected to check for null and re-prompt -
     * this is where "input validation" for status actually happens.
     */
    public static TaskStatus fromLabel(String input) {
        for (TaskStatus status : values()) {
            if (status.label.equalsIgnoreCase(input.trim())) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}
