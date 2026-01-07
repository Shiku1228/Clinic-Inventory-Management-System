package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import static java.nio.file.Files.list;
import models.Users;
import org.bson.Document;
import org.bson.types.ObjectId;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    private final MongoCollection<Document> usersCollection;

    public UsersDAO(MongoDatabase database) {
        this.usersCollection = database.getCollection("Users Collection");
    }

    // insert a new user
    public boolean insertUser(Users user) {
        try {
            Document doc = new Document("_id", new ObjectId())
                    .append("userId", user.getUserId())
                    .append("username", user.getName())
                    .append("role", user.getRole())
                    .append("contact", user.getContact())
                    .append("email", user.getEmail())
                    .append("status", user.getStatus())
                    .append("avatar", user.getAvatarPath())
                    .append("password", user.getPassword());
                    

            usersCollection.insertOne(doc);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //get all the user
    public List<Users> getAllUser() {
        List<Users> list = new ArrayList<>();
        for (Document doc : usersCollection.find()) {
            Users user = new Users(
                    doc.getObjectId("_id"),
                    doc.getString("userId"),
                    doc.getString("username"),
                    doc.getString("role"),
                    doc.getString("contact"),
                    doc.getString("email"),
                    doc.getString("status"),
                    doc.getString("avatar"),
                    doc.getString("password")
            );
            list.add(user);
        }
        return list;
    }

    //find user by username
    public Users getUserByUsername(String username) {
        Document doc = usersCollection.find(Filters.eq("username", username)).first();
        if (doc != null) {
            return new Users(
                    doc.getObjectId("_id"),
                    doc.getString("userId"),
                    doc.getString("username"),
                    doc.getString("role"),
                    doc.getString("contact"),
                    doc.getString("email"),
                    doc.getString("status"),
                    doc.getString("avatar"),
                    doc.getString("password")
            );
        }
        return null;
    }

    //update user info
    // Update user info
    public boolean updateUser(Users user) {

        if (user == null || user.getUserId() == null) {
            System.out.println("Cannot update user: ID is NULL");
            return false;
        }
        try {
            UpdateResult result = usersCollection.updateOne(
                    Filters.eq("userId", user.getUserId()),
                    Updates.combine(
                            Updates.set("username", user.getName()),
                            Updates.set("role", user.getRole()),
                            Updates.set("contact", user.getContact()),
                            Updates.set("email", user.getEmail()),
                            Updates.set("status", user.getStatus()),
                            Updates.set("avatar", user.getAvatarPath())
                    )
            );

            if (result.getMatchedCount() == 0) {
                System.out.println("No user found with ID: " + user.getUserId());
                return false;
            }

            System.out.println("User Updated: " + user.getUserId());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete user
    public boolean deleteUser(String userId) {
        try {
            DeleteResult result = usersCollection.deleteOne(Filters.eq("userId", userId));
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateNextUserId() {

        Document lastUser = usersCollection.find()
                .sort(new Document("userId", -1))
                .first();

        // ✅ No users at all
        if (lastUser == null) {
            return "USR_001";
        }

        String lastId = lastUser.getString("userId");

        // ✅ Users exist but userId is missing
        if (lastId == null || !lastId.contains("_")) {
            return "USR_001";
        }

        int num = Integer.parseInt(lastId.split("_")[1]);
        num++;

        return String.format("USR_%03d", num);
    }

    public Document authenticate(String username, String password) {
        return usersCollection.find(
                new Document("username", username)
                        .append("password", password)
        ).first();
    }

}
