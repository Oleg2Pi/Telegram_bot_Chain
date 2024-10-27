package by.polikarpov.repository;

import java.util.List;

public interface Dao<K, T> {
    public void save(T entity);
    public void delete(T entity);
    public void update(T entity);
    public T find(K id);
    public List<T> findAll();
}
