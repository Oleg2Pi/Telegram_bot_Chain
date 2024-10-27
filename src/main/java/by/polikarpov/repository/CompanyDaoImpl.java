package by.polikarpov.repository;

import by.polikarpov.entity.Company;
import by.polikarpov.utils.HibernateUtil;

import java.util.List;

public class CompanyDaoImpl implements Dao<Long, Company> {
    @Override
    public void save(Company entity) {
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
    public void delete(Company entity) {

    }

    @Override
    public void update(Company entity) {

    }

    @Override
    public Company find(Long id) {
        return null;
    }

    @Override
    public List<Company> findAll() {
        return List.of();
    }
}
