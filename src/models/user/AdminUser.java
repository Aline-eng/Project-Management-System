package models.user;

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
        return true;
    }
}
