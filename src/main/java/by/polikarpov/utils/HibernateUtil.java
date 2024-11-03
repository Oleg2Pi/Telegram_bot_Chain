package by.polikarpov.utils;

import by.polikarpov.entity.Person;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final Configuration config = new Configuration().configure();

    public static Session getSession() {
        config.addAnnotatedClass(Person.class);
        return config.buildSessionFactory().openSession();
    }
}
