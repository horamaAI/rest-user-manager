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

    @Autowired
    private IUserDAO userDAO;
    @Autowired
    private ModelMapper modelMapper;


    public UserDto registerUser(@RequestBody UserCreationDto userCreationDto) {
        System.out
                .println("xoxo in userservice.registerUser, param: " + userCreationDto.toString());
        if (userDAO.findByEmail(userCreationDto.getEmail()).isPresent()) {
            System.out.println("in duplicate email found");
            throw new InvalidUserException("Email already exists");
        }
        System.out.println("xoxo in userservice.registerUser 2");

        if (!isValidAgeAndCountry(userCreationDto.getBirthDate(), userCreationDto.getCountry())) {
            throw new InvalidUserException(
                    "User must be over 18 years old and live in France to create an account");
        }

        System.out.println("xoxo in userservice.registerUser 3");
        User user = new User(userCreationDto.getFirstName(), userCreationDto.getLastName(),
                userCreationDto.getEmail(), userCreationDto.getPassword(),
                userCreationDto.getCountry(), userCreationDto.getBirthDate());
        System.out.println("xoxo in userservice.registerUser 4");
        System.out.println("xoxo dao is null ? : " + (userDAO == null));
        User savedUser = userDAO.save(user);
        System.out.println("xoxo check 12 12, userDto " + savedUser.getBirthDate().toString());
        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        System.out.println("xoxo check 12 12, userDto " + userDto.getBirthDate().toString());
        return userDto;
    }

    public UserDto getUserById(String id) throws NotFoundException {
        Optional<User> optionalUser = userDAO.findById(id);
        System.out.println("xoxo so it begins!!!");
        if (optionalUser.isPresent()) {
            System.out.println("xoxo WTF!!!");
            return modelMapper.map(optionalUser.get(), UserDto.class);
        } else {
            System.out.println("xoxo YAAAAS!!!");
            throw new NotFoundException("User not found");
        }
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userDAO.findAll();
        System.out.println("xoxo users size " + users.size());
        for (User user : users) {
            System.out.println("xoxo user " + user.getFirstName());
        }

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
        int age = Period.between(birthDate, todayDate).getYears();
        boolean isInFrance = country.equals("France");
        System.out.println("country : " + country + ", is in france ? :" + isInFrance);
        return (age >= 18 && isInFrance);

    }
}
