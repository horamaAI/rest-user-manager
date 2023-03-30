package com.peppermint.restusermanager.dao;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.peppermint.restusermanager.model.*;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByEmail(String email);
}
