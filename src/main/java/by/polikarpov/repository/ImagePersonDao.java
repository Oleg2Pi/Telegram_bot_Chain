package by.polikarpov.repository;

import by.polikarpov.entity.ImagePerson;
import by.polikarpov.utils.HibernateUtil;
import org.hibernate.Session;

public class ImagePersonDao implements Dao<ImagePerson>{

    @Override
    public ImagePerson save(ImagePerson image) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();

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

        return image;
    }
}
