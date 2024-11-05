package by.polikarpov.repository;

public interface Dao<T> {
    public T save(T entity);
}
