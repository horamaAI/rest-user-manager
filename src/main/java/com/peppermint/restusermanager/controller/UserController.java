package com.peppermint.restusermanager.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.peppermint.restusermanager.aspect.LogExecutionTime;
import com.peppermint.restusermanager.dto.UserCreationDto;
import com.peppermint.restusermanager.dto.UserDto;
import com.peppermint.restusermanager.exceptions.BadRequestException;
import com.peppermint.restusermanager.exceptions.NotFoundException;
import com.peppermint.restusermanager.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping(value = "register")
    @LogExecutionTime
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreationDto userCreationDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            BadRequestException badRequestResponse = new BadRequestException(errors);
            logger.warn("Failed to register user: [\"{}\"]", userCreationDto.getFirstName(),
                    badRequestResponse);
            return ResponseEntity.badRequest().body(badRequestResponse);
        }

        if (!userService.isValidAgeAndCountry(userCreationDto.getBirthDate(),
                userCreationDto.getCountry())) {
            BadRequestException badRequestResponse = new BadRequestException(
                    "User must be over 18 years old and living in France to register.");
            logger.warn("Failed to register user: {}", userCreationDto.getFirstName(),
                    badRequestResponse);
            return ResponseEntity.badRequest().body(badRequestResponse);
        }

        UserDto userDto = userService.registerUser(userCreationDto);
        logger.info("Registered user with email: {}", userCreationDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        System.out.println("xoxo spider-cochon");
        try {
            UserDto userDto = userService.getUserById(id);
            logger.info("Retrieved user with email: {}", userDto.getEmail());
            return ResponseEntity.ok(userDto);
        } catch (NotFoundException notFoundExc) {
            // If no user is found with the given ID, return a NOT_FOUND response
            NotFoundException notFoundResponse =
                    new NotFoundException("User not found with id: " + id);
            logger.warn("Failed to retrieve user with id {}: {}", id, notFoundResponse);
            ResponseEntity<?> res =
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            System.out.println("xoxo spider-cochon OUPS!! notfoundresponse to string "
                    + notFoundExc.toString());
            System.out.println(
                    "xoxo spider-cochon OUPS!! response entity to string " + res.toString());
            return res;
        }
    }

    @GetMapping("/list-users")
    @LogExecutionTime
    public ResponseEntity<List<UserDto>> getAllUsers() {

        List<UserDto> users = userService.getAllUsers();
        logger.info("Retrieved all users");
        return ResponseEntity.ok(users);
    }
}
