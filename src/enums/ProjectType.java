package enums;

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
