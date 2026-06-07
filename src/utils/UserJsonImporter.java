package utils;

import com.mongodb.client.MongoDatabase;
import dao.UsersDAO;
import database.MongoDBConnection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import models.Users;
import org.bson.Document;
import org.bson.types.ObjectId;

public final class UserJsonImporter {

    private UserJsonImporter() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java utils.UserJsonImporter <path-to-users.json>");
            System.exit(1);
        }

        Path jsonPath = Paths.get(args[0]);

        try {
            int imported = importUsers(jsonPath);
            System.out.println("Imported/updated " + imported + " user record(s).");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static int importUsers(Path jsonPath) throws IOException {
        String raw = Files.readString(jsonPath, StandardCharsets.UTF_8);
        List<String> objectJsons = extractJsonObjects(raw);

        MongoDatabase database = MongoDBConnection.getDatabase();
        UsersDAO usersDAO = new UsersDAO(database);

        int imported = 0;
        for (String objectJson : objectJsons) {
            Document doc = Document.parse(objectJson);
            Users user = toUser(doc, usersDAO);
            if (usersDAO.upsertUser(user)) {
                imported++;
            }
        }

        return imported;
    }

    private static Users toUser(Document doc, UsersDAO usersDAO) {
        ObjectId mongoId = null;
        Object rawId = doc.get("_id");
        if (rawId instanceof ObjectId) {
            mongoId = (ObjectId) rawId;
        } else if (rawId instanceof String) {
            try {
                mongoId = new ObjectId((String) rawId);
            } catch (IllegalArgumentException ignored) {
                mongoId = null;
            }
        }

        String userId = firstNonEmpty(
                doc.getString("userId"),
                doc.getString("user_id"),
                doc.getString("id")
        );
        if (userId == null || userId.isBlank()) {
            userId = usersDAO.generateNextUserId();
        }

        String name = firstNonEmpty(
                doc.getString("username"),
                doc.getString("name")
        );

        String role = firstNonEmpty(doc.getString("role"), "Admin");
        String contact = firstNonEmpty(doc.getString("contact"), "");
        String email = firstNonEmpty(doc.getString("email"), "");
        String status = firstNonEmpty(doc.getString("status"), "Active");
        String avatarPath = firstNonEmpty(
                doc.getString("avatar"),
                doc.getString("avatarPath"),
                "/resource/avatars/user.png"
        );
        String password = firstNonEmpty(doc.getString("password"), "");

        return new Users(
                mongoId,
                userId,
                name,
                role,
                contact,
                email,
                status,
                avatarPath,
                password
        );
    }

    private static List<String> extractJsonObjects(String raw) throws IOException {
        String content = raw.replace("\uFEFF", "").trim();
        if (content.isEmpty()) {
            return List.of();
        }

        if (content.startsWith("{")) {
            return List.of(content);
        }

        if (!content.startsWith("[")) {
            throw new IOException("Expected a JSON array or object.");
        }

        List<String> objects = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;
        int depth = 0;

        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);

            if (inString) {
                current.append(ch);
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }

            if (ch == '"') {
                inString = true;
                if (depth > 0) {
                    current.append(ch);
                }
                continue;
            }

            if (ch == '{') {
                depth++;
                current.append(ch);
                continue;
            }

            if (ch == '}') {
                current.append(ch);
                depth--;
                if (depth == 0) {
                    objects.add(current.toString().trim());
                    current.setLength(0);
                }
                continue;
            }

            if (depth > 0) {
                current.append(ch);
            }
        }

        return objects;
    }

    private static String firstNonEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
