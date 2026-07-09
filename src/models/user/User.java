package models.user;

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

    public abstract String getPermissions();

    public abstract boolean canModify();

    public void displayUserInfo() {
        System.out.println("User ID: " + id);
        System.out.println("Name: " + name + " (" + role + ")");
        System.out.println("Email: " + email);
        System.out.println("Permissions: " + getPermissions());
    }
}
