package com.peppermint.restusermanager.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.peppermint.restusermanager.dao.IUserDAO;
import com.peppermint.restusermanager.dto.UserCreationDto;
import com.peppermint.restusermanager.dto.UserDto;
import com.peppermint.restusermanager.exceptions.InvalidUserException;
import com.peppermint.restusermanager.exceptions.NotFoundException;
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

    public UserDto registerUser(@RequestBody UserCreationDto userCreationDto) {
        if (userDAO.findByEmail(userCreationDto.getEmail()).isPresent()) {
            throw new InvalidUserException("Email already exists");
        }

        if (!isValidAgeAndCountry(userCreationDto.getBirthDate(), userCreationDto.getCountry())) {
            throw new InvalidUserException(
                    "User must be over 18 years old and live in France to create an account");
        }

        User user = new User(userCreationDto.getFirstName(), userCreationDto.getLastName(),
                userCreationDto.getEmail(), userCreationDto.getPassword(),
                userCreationDto.getBirthDate(), userCreationDto.getCountry());
        User savedUser = userDAO.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public UserDto getUserById(String id) {
        Optional<User> optionalUser = userDAO.findById(id);
        if (optionalUser.isPresent()) {
            return modelMapper.map(optionalUser.get(), UserDto.class);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userDAO.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
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

    public boolean isValidAgeAndCountry(LocalDate birthDate, String country) {
        LocalDate todayDate = LocalDate.now();
        return (Period.between(birthDate, todayDate).getYears() >= 18 && country.equals("France"));

    }
}
