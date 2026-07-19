package services;

import models.user.User;

/**
 * Owns the in-memory user directory: storage and lookup for every user
 * registered in the system. Mirrors ProjectService's array + count pattern.
 */
public class UserService {
    private User[] users;
    private int userCount;
    private static final int MAX_USERS = 50;

    public UserService() {
        users = new User[MAX_USERS];
        userCount = 0;
    }

    /**
     * @return true if added; false if user storage is full
     */
    public boolean addUser(User user) {
        if (userCount >= users.length) {
            System.out.println("User storage is full.");
            return false;
        }
        users[userCount] = user;
        userCount++;
        return true;
    }

    /**
     * @return a defensive copy of every registered user
     */
    public User[] getAllUsers() {
        User[] result = new User[userCount];
        System.arraycopy(users, 0, result, 0, userCount);
        return result;
    }

    public int getUserCount() {
        return userCount;
    }
}
