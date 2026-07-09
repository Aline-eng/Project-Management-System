package enums;


public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TaskStatus fromLabel(String input) {
        for (TaskStatus status : values()) {
            if (status.label.equalsIgnoreCase(input.trim())) {
                return status;
            }
        }
        return null;
    }
}
