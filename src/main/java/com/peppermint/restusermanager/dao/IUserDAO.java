package com.peppermint.restusermanager.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.peppermint.restusermanager.model.User;

@Repository
public interface IUserDAO extends MongoRepository<User, String> {

    public Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    public Optional<User> findByEmail(String email);

    public List<User> findAll();

    public Optional<User> findById(int id);

    // public User add(User user);

    // public User update(User user);

    public boolean deleteById(int id);

}
