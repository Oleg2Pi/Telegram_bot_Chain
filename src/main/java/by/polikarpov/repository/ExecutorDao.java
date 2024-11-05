package by.polikarpov.repository;

import by.polikarpov.entity.Executor;
import by.polikarpov.utils.HibernateUtil;
import org.hibernate.Session;

public class ExecutorDao {

    public void save(Executor executor) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();

            session.save(executor);

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
