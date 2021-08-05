package db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class ConnectionMongoDb {

    private static MongoClient mongoClient;

    private ConnectionMongoDb() {
    }

    public static MongoClient getConnection() {
        if (mongoClient == null) {
            mongoClient = new MongoClient("localhost", 27017);
        }
        return mongoClient;
    }

    public static MongoDatabase getDataBase(String dataBaseName) {
        MongoDatabase db = getConnection().getDatabase(dataBaseName);
        return db;
    }

    public static MongoDatabase getDefaultDataBase() {
        return getDataBase(DataBaseName.DEFAULT_DATA_BASE_NAME.getName());
    }
}



