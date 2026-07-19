package models.user;

import utils.exceptions.InvalidUserDataException;

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

    private static final String EMAIL_PATTERN = "^\\S+@\\S+\\.\\S+$";

    /**
     * @throws InvalidUserDataException if name is blank, email doesn't look
     *         like an email, or role is blank. Same idea as Project's
     *         constructor check - the class guards its own invariants
     *         instead of trusting the caller to have validated already.
     */
    protected User(String name, String email, String role) throws InvalidUserDataException {
        if (name == null || name.isBlank()) {
            throw new InvalidUserDataException("User name cannot be empty.");
        }
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            throw new InvalidUserDataException("Email '" + email + "' is not a valid email address.");
        }
        if (role == null || role.isBlank()) {
            throw new InvalidUserDataException("User role cannot be empty.");
        }
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
