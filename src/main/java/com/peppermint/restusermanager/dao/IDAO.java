package com.peppermint.restusermanager.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDAO<T> extends MongoRepository<T, String> {
    public List<T> findAll();

    public Optional<T> findById(int id);

    public T add(T entity);

    public T update(T entity);

    public boolean deleteById(int id);
}
