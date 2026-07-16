package models.user;

/**
 * Abstract base class for a system user. Defines the shared identity
 * (id, name, email, role) every user type has, and leaves permissions
 * and modify-rights abstract so each role defines its own access level.
 */
public abstract class User {

    private String id;
    private String name;
    private String email;
    private String role;

    private static int userCounter = 0;

    protected User(String name, String email, String role) {
        userCounter++;
        this.id = String.format("USR%03d", userCounter);
        this.name = name;
        this.email = email;
        this.role = role;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    /** Human-readable summary of what this role can do, for display. */
    public abstract String getPermissions();

    /**
     * @return true if this role may update or delete tasks/projects,
     *         false if it can only view and add
     */
    public abstract boolean canModify();

    public void displayUserInfo() {
        System.out.println("User ID: " + id);
        System.out.println("Name: " + name + " (" + role + ")");
        System.out.println("Email: " + email);
        System.out.println("Permissions: " + getPermissions());
    }
}
