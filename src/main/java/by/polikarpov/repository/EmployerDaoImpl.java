package by.polikarpov.repository;

import by.polikarpov.entity.Employer;
import by.polikarpov.utils.HibernateUtil;

import java.util.List;

public class EmployerDaoImpl implements Dao<Long, Employer> {
    @Override
    public void save(Employer entity) {
        try {
            var session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Employer entity) {

    }

    @Override
    public void update(Employer entity) {

    }

    @Override
    public Employer find(Long id) {
        return null;
    }

    @Override
    public List<Employer> findAll() {
        return List.of();
    }
}
