package by.polikarpov.repository;

import by.polikarpov.entity.ImagePerson;
import by.polikarpov.entity.Person;
import by.polikarpov.utils.HibernateUtil;
import org.hibernate.Session;

public class PersonDao {

    public void save(Person person, ImagePerson image) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();

            session.save(person);
            session.save(image);

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }
}
