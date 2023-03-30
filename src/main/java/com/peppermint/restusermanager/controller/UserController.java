package com.peppermint.restusermanager.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peppermint.restusermanager.model.User;
import com.peppermint.restusermanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDto userDto,
            BindingResult bindingResult,
            @RequestParam(required = false, defaultValue = "true") Boolean newsletter,
            @RequestParam(required = false, defaultValue = "false") Boolean isAdmin) {

        long startTime = System.currentTimeMillis();

        if (bindingResult.hasErrors()) {
            // If there are validation errors, return a BAD_REQUEST response with the error details
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errors);
            logger.warn("Failed to register user: {}", errorResponse);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (!userService.isValidAgeAndCountry(userDto.getAge(), userDto.getCountry())) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                    "User must be over 18 years old and living in France to register.");
            logger.warn("Failed to register user: {}", errorResponse);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        User user = userService.registerUser(userDto);
        logger.info("Registered user with email: {}", user.getEmail());
        long endTime = System.currentTimeMillis();
        logger.info("Execution time for registerUser: {} ms", (endTime - startTime));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        long startTime = System.currentTimeMillis();
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {

            User user = userOptional.get();
            logger.info("Retrieved user with email: {}", user.getEmail());
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for getUserById: {} ms", (endTime - startTime));
            return ResponseEntity.ok(user);
        } else {
            // If no user is found with the given ID, return a NOT_FOUND response
            ErrorResponse errorResponse =
                    new ErrorResponse(HttpStatus.NOT_FOUND, "User not found with id: " + id);
            logger.warn("Failed to retrieve user with id {}: {}", id, errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        long startTime = System.currentTimeMillis();

        List<User> users = userService.getAllUsers();
        logger.info("Retrieved all users");
        long endTime = System.currentTimeMillis();
        logger.info("Execution time for getAllUsers: {} ms", (endTime - startTime));
        return ResponseEntity.ok(users);
    }
}
