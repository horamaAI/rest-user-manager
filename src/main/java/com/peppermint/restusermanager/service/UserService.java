package com.peppermint.restusermanager.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.asm.Advice.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.peppermint.dto.UserDto;
import com.peppermint.exceptions.BadRequestException;
import com.peppermint.exceptions.NotFoundException;
import com.peppermint.restusermanager.dao.IUserDAO;
import com.peppermint.restusermanager.dao.UserRepository;
import com.peppermint.restusermanager.model.User;

@Service
public class UserService {

    private IUserDAO userDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(IUserDAO userDAO, ModelMapper modelMapper) {
        this.userDAO = userDAO;
        this.modelMapper = modelMapper;
    }

    public UserDto getUserById(String id) {
        Optional<User> optionalUser = userDAO.findById(id);
        if (optionalUser.isPresent()) {
            return modelMapper.map(optionalUser.get(), UserDto.class);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> getByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public User save(User user) {
        return userDAO.save(user);
    }

    public void deleteById(String id) {
        userDAO.deleteById(id);
    }

    public boolean existsById(String id) {
        return userDAO.existsById(id);
    }


    public UserDto registerUser(String firstName, String lastName, String email, String password,
            LocalDate birthDate, String country) {
        if (userDAO.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        if (!isValidAgeAndCountry(birthDate, country)) {
            throw new BadRequestException(
                    "User must be over 18 years old and live in France to create an account");
        }

        User user = new User(firstName, lastName, email, password, birthDate, country);
        User savedUser = userDAO.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    private boolean isValidAgeAndCountry(LocalDate birthDate, String country) {
        LocalDate todayDate = LocalDate.now();
        if (Period.between(birthDate, todayDate).getYears() >= 18 && country.equals("France")) {
            return true;
        }
        return false;

    }
}
