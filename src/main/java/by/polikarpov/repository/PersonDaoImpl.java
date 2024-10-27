package by.polikarpov.repository;

import by.polikarpov.entity.Person;
import by.polikarpov.utils.HibernateUtil;

import java.util.List;

public class PersonDaoImpl implements Dao<Long, Person> {

    @Override
    public void save(Person person) {
        try {
            var session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(person);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Person entity) {

    }

    @Override
    public void update(Person entity) {

    }

    @Override
    public Person find(Long id) {
        return null;
    }

    @Override
    public List<Person> findAll() {
        return List.of();
    }
}
