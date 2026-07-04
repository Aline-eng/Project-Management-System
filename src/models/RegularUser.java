package models;

/**
 * Concrete subclass of User representing a standard (non-admin) user.
 *
 * OOP CONCEPT - INHERITANCE + POLYMORPHISM:
 *   Reuses id/name/email/role/displayUserInfo() from User, and overrides
 *   getPermissions()/canModify() with restricted, view-and-add-only behavior.
 */
public class RegularUser extends User {

    public RegularUser(String name, String email) {
        super(name, email, "Regular");
    }

    @Override
    public String getPermissions() {
        return "View, Add";
    }

    @Override
    public boolean canModify() {
        // Regular users cannot update or delete projects/tasks
        return false;
    }
}
