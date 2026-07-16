package enums;

/**
 * The fixed, valid set of project types. A closed, compiler-checked
 * alternative to a raw String field, mirroring {@link TaskStatus}.
 */
public enum ProjectType {
    SOFTWARE("Software"),
    HARDWARE("Hardware");

    private final String label;

    ProjectType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Used by every printf("%s", ...) call site that prints a project's
     * type directly (e.g. Project.displayProject()), so those call sites
     * don't need to remember to call getLabel() themselves.
     */
    @Override
    public String toString() {
        return label;
    }
}
