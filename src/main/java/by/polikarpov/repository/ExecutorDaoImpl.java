package by.polikarpov.repository;

import by.polikarpov.entity.Executor;
import by.polikarpov.utils.HibernateUtil;

import java.util.List;

public class ExecutorDaoImpl implements Dao<Long, Executor>{

    @Override
    public void save(Executor entity) {
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
    public void delete(Executor entity) {

    }

    @Override
    public void update(Executor entity) {

    }

    @Override
    public Executor find(Long id) {
        return null;
    }

    @Override
    public List<Executor> findAll() {
        return List.of();
    }
}
