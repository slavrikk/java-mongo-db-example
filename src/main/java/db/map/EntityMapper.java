package db.map;

import db.entity.Job;
import db.entity.User;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EntityMapper {

    public static User mapDocToUser(Document doc) {
        User user = new User();
        user.setName((String) doc.get("name"));
        user.setLastName((String) doc.get("lastName"));
        user.setEmail((String) doc.get("email"));
        user.setAge((Integer) doc.get("age"));
        if (doc.get("job") != null) {
            Document jobDoc = (Document) doc.get("job");
            Job job = new Job();
            job.setName((String) jobDoc.get("name"));
            job.setSalary((Integer) jobDoc.get("salary"));
            job.setHiredDate(LocalDate.ofInstant(((Date) jobDoc.get("hiredDate")).toInstant(), ZoneId.systemDefault()));
            user.setJob(job);
        }
        return user;
    }

    public static Document mapUserToDoc(User user) {
        Document document = new Document();
        document.append("name", user.getName())
                .append("lastName", user.getLastName())
                .append("email", user.getEmail())
                .append("age",user.getAge());
        if (user.getJob() != null) {
            document.append("job", new Document()
                    .append("name", user.getJob().getName())
                    .append("hiredDate", user.getJob().getHiredDate())
                    .append("salary", user.getJob().getSalary()));
        }
        return document;
    }
}
