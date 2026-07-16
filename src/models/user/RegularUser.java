package models.user;

/** A standard user: can view and add, but not update or delete. */
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
        return false;
    }
}
