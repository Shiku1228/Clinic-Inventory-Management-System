package utils;

import org.bson.Document;

public class Session {
    private static Document currentUser;

    public static void setCurrentUser(Document user) {
        currentUser = user;
    }

    public static Document getCurrentUser() {
        return currentUser;
    }

    public static String getRole() {
        if (currentUser != null && currentUser.containsKey("role")) {
            return currentUser.getString("role");
        }
        return null;
    }

    public static String getUsername() {
        if (currentUser != null && currentUser.containsKey("username")) {
            return currentUser.getString("username");
        }
        return "Guest";
    }
}
