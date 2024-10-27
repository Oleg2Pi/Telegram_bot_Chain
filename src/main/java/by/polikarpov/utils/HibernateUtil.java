package by.polikarpov.utils;

import by.polikarpov.entity.Company;
import by.polikarpov.entity.Employer;
import by.polikarpov.entity.Executor;
import by.polikarpov.entity.Person;
import org.glassfish.grizzly.nio.SelectorHandler;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final Configuration config = new Configuration().configure();

    public static Session getSession() {
        config.addAnnotatedClass(Person.class);
        config.addAnnotatedClass(Executor.class);
        config.addAnnotatedClass(Employer.class);
        config.addAnnotatedClass(Company.class);
        return config.buildSessionFactory().openSession();
    }

    public static void closeSession() {
        getSession().close();
    }
}
