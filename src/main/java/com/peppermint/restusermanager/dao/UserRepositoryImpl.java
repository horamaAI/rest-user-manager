package com.peppermint.restusermanager.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import com.peppermint.restusermanager.model.User;

public class UserRepositoryImpl extends UserRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean isUserAllowedToRegister(User user) {
        if (user.getDateOfBirth() == null || user.getCountry() == null) {
            return false;
        }

        // Check age is over 18 years old
        Period age = Period.between(user.getDateOfBirth(), LocalDate.now());
        if (age.getYears() < 18) {
            return false;
        }

        // Check if country is France
        if (!user.getCountry().equalsIgnoreCase("France")) {
            return false;
        }

        return true;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAllUsers() {
        return mongoTemplate.findAll(User.class);
    }
}
