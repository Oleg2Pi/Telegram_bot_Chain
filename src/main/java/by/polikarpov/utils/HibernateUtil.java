package by.polikarpov.utils;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final Configuration config = new Configuration().configure();

    public static Session getSession() {

        return config.buildSessionFactory().openSession();
    }

    public static void closeSession() {
        getSession().close();
    }
}
