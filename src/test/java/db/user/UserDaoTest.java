package db.user;


import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneModel;
import db.ConnectionMongoDb;
import db.dao.UserDao;
import db.entity.Job;
import db.entity.User;
import db.map.EntityMapper;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static db.DataBaseName.DEFAULT_DATA_BASE_NAME;

public class UserDaoTest {

    private static UserDao userDao;

    private static MongoCollection<Document> userCollection;

    @Before
    public void generateTestData() {
        userDao = new UserDao();
        userCollection = ConnectionMongoDb
                .getDataBase(DEFAULT_DATA_BASE_NAME.getName())
                .getCollection(UserDao.USERS_DB_NAME);


        User user2 = new User("Kolya", "Ivanov", 20,
                "email@mail.ru",
                new Job("Developer", LocalDate.of(2020, 9, 20), 1000));


        User user1 = new User("Vasya", "Petrov", 18,
                "test@mail.ru",
                new Job("Trainee", LocalDate.of(2021, 10, 14), 300));

        User user4 = new User("Alex", "Federov", 35,
                "mail@mail.ru",
                new Job("CEO", LocalDate.of(2017, 5, 5), 3000));

        User user3 = new User("Ivan", "Sidorov", 28,
                "someeamil@mail.ru",
                new Job("Lead", LocalDate.of(2019, 1, 10), 2000));

        BulkWriteResult result = userCollection.bulkWrite(
                Arrays.asList(
                        new InsertOneModel<>(EntityMapper.mapUserToDoc(user1)),
                        new InsertOneModel<>(EntityMapper.mapUserToDoc(user2)),
                        new InsertOneModel<>(EntityMapper.mapUserToDoc(user3)),
                        new InsertOneModel<>(EntityMapper.mapUserToDoc(user4))));

        Assert.assertTrue(result.wasAcknowledged());
    }

    @Test
    public void searchUsersByAgeRangeTest() {
        List<User> userList = userDao.findUsersByAgeRange(18, 28);
        Assert.assertEquals(3, userList.size());
        dropUserCollection();
    }

    @Test
    public void searchUserByName() {
        String searchName = "Kolya";
        User user = userDao.findUserByName(searchName);
        Assert.assertEquals(searchName, user.getName());
        dropUserCollection();
    }

    @Test
    public void insertUserAndFindTest() {
        dropUserCollection();
        User user = new User("Vasya", "Petrov", 18,
                "test@mail.ru",
                new Job("Trainee", LocalDate.of(2021, 10, 14), 300));
        userDao.insertOneUser(user);
        User userFromDb = userDao.findUserByName(user.getName());
        Assert.assertEquals(user.getName(), userFromDb.getName());
        Assert.assertEquals(user.getLastName(), userFromDb.getLastName());
        Assert.assertEquals(user.getJob().getSalary(), userFromDb.getJob().getSalary());
        dropUserCollection();
    }

    public void dropUserCollection() {
        userCollection.drop();
    }
}
