package db.dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import db.entity.User;
import db.map.EntityMapper;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static db.ConnectionMongoDb.getDefaultDataBase;
import static db.map.EntityMapper.mapDocToUser;
import static db.map.EntityMapper.mapUserToDoc;

public class UserDao {

    public static final String USERS_DB_NAME = "users";

    private static final Logger LOGGER = Logger.getLogger(UserDao.class);

    //Insert User
    public void insertOneUser(User user) {
        try {
            getCollection().insertOne(mapUserToDoc(user));
        } catch (MongoException me) {
            LOGGER.error("Unable to update due to an error: " + me);
        }
    }

    //Insert A lot of Users
    public void insertManyUsers(List<User> users) {
        try {
            List<Document> usersDocs = users.stream()
                    .map(EntityMapper::mapUserToDoc)
                    .collect(Collectors.toList());
            getCollection().insertMany(usersDocs);
        } catch (MongoException me) {
            LOGGER.error("Unable to update due to an error: " + me);
        }
    }

    //Update User`s email
    public Boolean updateUserEmail(String currentEmail, String newEmail) {
        Document query = new Document().append("email", currentEmail);
        Bson updates = Updates.combine(
                Updates.set("email", newEmail));
        boolean result = false;
        try {
            UpdateResult updateResult = getCollection().updateOne(query, updates);
            result = updateResult.wasAcknowledged();
            LOGGER.debug("Modified document count: " + updateResult.getModifiedCount());
        } catch (MongoException me) {
            LOGGER.error("Unable to update due to an error: " + me);
        }
        return result;
    }

    //Delete User by FullName
    public boolean deleteUserByFullName(String name, String lastName) {
        Bson query = and(
                eq("name", name),
                eq("lastName", lastName));

        boolean result = false;
        try {
            DeleteResult deleteResult = getCollection().deleteOne(query);
            result = deleteResult.wasAcknowledged();

            LOGGER.debug("Deleted document count: " + deleteResult.getDeletedCount());
        } catch (MongoException me) {
            LOGGER.error("Unable to delete due to an error: " + me);
        }
        return result;
    }

    //Find User by Name
    public User findUserByName(String name) {
        Document doc = null;
        try {
            doc = (Document) getCollection().find(eq("name", name))
                    .projection(getProjections())
                    .first();
        } catch (MongoException me) {
            LOGGER.error("Unable to update due to an error: " + me);
        }

        if (doc != null) {
            return mapDocToUser(doc);
        }

        return null;
    }

    //Find Users between specified age range
    public List<User> findUsersByAgeRange(int from, int to) {

        List<User> userList = new ArrayList<>();

        try (MongoCursor<Document> cursor = getCollection()
                .find(and(lte("age", to), gte("age", from)))
                .projection(getProjections())
                .sort(Sorts.ascending("age")).iterator()) {
            while (cursor.hasNext()) {
                userList.add(mapDocToUser(cursor.next()));
            }
        } catch (MongoException me) {
            LOGGER.error("Unable to update due to an error: " + me);
        }

        return userList;
    }

    private MongoCollection getCollection() {
        return getDefaultDataBase().getCollection(USERS_DB_NAME);
    }

    private Bson getProjections() {
        return Projections.fields(
                Projections.include("name", "lastName", "age", "email", "job"),
                Projections.excludeId());
    }

}
