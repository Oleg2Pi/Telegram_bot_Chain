package by.polikarpov.utils;

import by.polikarpov.entity.Person;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration config = new Configuration().configure();
            config.addAnnotatedClass(Person.class);
            sessionFactory = config.buildSessionFactory();
        } catch (HibernateException e) {
            System.err.println("Initial SessionFactory creation failed: " + e);
            throw new RuntimeException(e);
        }
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
