package com.peppermint.restusermanager.dao;


import java.util.Optional;
import com.peppermint.restusermanager.model.User;

public interface IUserDAO extends IDAO<User> {

    public Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    public Optional<User> findByEmail(String email);

}
