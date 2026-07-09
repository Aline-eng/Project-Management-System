package models;

/**
 * Enum representing the fixed, valid set of project types.
 *
 * Same reasoning as TaskStatus: a closed, compiler-checked set of constants
 * instead of raw strings, so "Software"/"Hardware" can't be mistyped
 * wherever a project's type needs to be created or compared.
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
}
