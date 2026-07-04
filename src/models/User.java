package models;

/**
 * Abstract base class representing a system user.
 *
 * OOP CONCEPT - ABSTRACTION: cannot be instantiated directly; defines the
 *   shared shape (id, name, email, role) every user type must have, and
 *   leaves getPermissions() abstract so each role defines its own access.
 * OOP CONCEPT - ENCAPSULATION: private fields, accessed only via getters/setters.
 */
public abstract class User {

    private String id;
    private String name;
    private String email;
    private String role;

    // STATIC FIELD: shared across ALL User objects (Regular or Admin), used to
    // auto-generate unique, sequential user IDs: USR001, USR002, USR003...
    private static int userCounter = 0;

    protected User(String name, String email, String role) {
        userCounter++;
        this.id = String.format("USR%03d", userCounter);
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // ---- Getters and setters (ENCAPSULATION) ----

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

    /**
     * ABSTRACTION: each concrete user type reports its own permission set.
     * Used later by services to enforce role-based access (Admin can
     * update/delete; Regular can only view/add).
     */
    public abstract String getPermissions();

    /**
     * Returns true if this user is allowed to perform destructive actions
     * (update/delete). Overridden per role - see RegularUser/AdminUser.
     */
    public abstract boolean canModify();

    /**
     * Concrete method shared by every user type - prints a standard identity
     * block. Because it calls getPermissions() (abstract), the OUTPUT differs
     * per subtype even though this method itself is written only once here.
     */
    public void displayUserInfo() {
        System.out.println("User ID: " + id);
        System.out.println("Name: " + name + " (" + role + ")");
        System.out.println("Email: " + email);
        System.out.println("Permissions: " + getPermissions());
    }
}
