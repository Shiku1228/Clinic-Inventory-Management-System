package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import static java.nio.file.Files.list;
import models.Users;
import org.bson.Document;
import org.bson.types.ObjectId;

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
                    .append("username", user.getName())
                    .append("role", user.getRole())
                    .append("contact", user.getContact())
                    .append("email", user.getEmail())
                    .append("status", user.getStatus())
                    .append("avatar", user.getAvatarPath());

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
                    doc.getObjectId("_id").toHexString(),
                    doc.getString("username"),
                    doc.getString("role"),
                    doc.getString("contact"),
                    doc.getString("email"),
                    doc.getString("status"),
                    doc.getString("avatar")
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
                    doc.getObjectId("_id").toHexString(),
                    doc.getString("username"),
                    doc.getString("role"),
                    doc.getString("contact"),
                    doc.getString("email"),
                    doc.getString("status"),
                    doc.getString("avatar")
            );
        }
        return null;
    }

    //update user info
    // Update user info
    public boolean updateUser(Users user) {
        try {
            usersCollection.updateOne(
                    Filters.eq("_id", new ObjectId(user.getId())),
                    Updates.combine(
                            Updates.set("username", user.getName()),
                            Updates.set("role", user.getRole()),
                            Updates.set("contact", user.getContact()),
                            Updates.set("email", user.getEmail()),
                            Updates.set("status", user.getStatus()),
                            Updates.set("avatar", user.getAvatarPath())
                    )
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete user
    public boolean deleteUser(String userId) {
        try {
            usersCollection.deleteOne(Filters.eq("_id", new ObjectId(userId)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
