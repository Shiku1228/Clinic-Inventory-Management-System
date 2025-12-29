
package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoDatabase database;
    
    public static MongoDatabase getDatabase(){
        if (database == null){
            MongoClient client = MongoClients.create("mongodb://localhost:27017");
            database = client.getDatabase("clinic_inventory");
        }
        return database;
    }
}
