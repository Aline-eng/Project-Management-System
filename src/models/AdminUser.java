package models;

/**
 * Concrete subclass of User representing an administrator with full access.
 *
 * OOP CONCEPT - POLYMORPHISM:
 *   Same method names as RegularUser (getPermissions(), canModify()), but
 *   entirely different behavior - this is the essence of overriding: one
 *   shared contract (defined in User), many possible implementations.
 */
public class AdminUser extends User {

    public AdminUser(String name, String email) {
        super(name, email, "Admin");
    }

    @Override
    public String getPermissions() {
        return "View, Add, Update, Delete";
    }

    @Override
    public boolean canModify() {
        // Admin users can update or delete projects/tasks
        return true;
    }
}
