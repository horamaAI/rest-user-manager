
package com.peppermint.restusermanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.peppermint.restusermanager.dao.IUserDAO;
import com.peppermint.restusermanager.dto.UserCreationDto;
import com.peppermint.restusermanager.dto.UserDto;
import com.peppermint.restusermanager.exceptions.InvalidUserException;
import com.peppermint.restusermanager.exceptions.NotFoundException;
import com.peppermint.restusermanager.model.User;

@SpringBootTest
@DisplayName("User service validation test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private IUserDAO userDAO;

    @MockBean
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userDAO, modelMapper);
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {

        UserCreationDto userCreationDto = new UserCreationDto("John", "Doe", "johndoe@example.com",
                LocalDate.now().minusYears(25), "password", "France");

        User user = new User(userCreationDto.getFirstName(), userCreationDto.getLastName(),
                userCreationDto.getEmail(), userCreationDto.getPassword(),
                userCreationDto.getBirthDate(), userCreationDto.getCountry());

        when(userDAO.save(user)).thenReturn(user);

        // Act
        UserDto resultDto = userService.registerUser(userCreationDto);

        // Assert
        assertNotNull(resultDto);
        assertEquals(user.getFirstName(), resultDto.getFirstName());
        assertEquals(user.getLastName(), resultDto.getLastName());
        assertEquals(user.getEmail(), resultDto.getEmail());
        assertEquals(user.getBirthDate(), resultDto.getBirthDate());
        assertEquals(user.getCountry(), resultDto.getCountry());
    }

    @Test
    public void testRegisterInvalidUser() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("John", "Doe", "johndoe@example.com",
                LocalDate.now().minusYears(15), "password", "Belgium");

        InvalidUserException thrown = Assertions.assertThrows(InvalidUserException.class, () -> {
            userService.registerUser(userCreationDto);
        });

        assertTrue(thrown.getMessage().contains("must be over 18 years"));
        assertTrue(thrown.getMessage().contains("live in France"));
    }

    @Test
    public void testGetUserDetailsSuccess() throws Exception {
        // Arrange
        User user = new User();
        user.setId("123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setCountry("France");

        userService.save(user);

        when(userDAO.findById("123")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getByEmail("john.doe@test.com");

        // Assert
        assertFalse(!result.isPresent());
        User userResult = result.get();
        assertEquals(user.getId(), userResult.getId());
        assertEquals(user.getFirstName(), userResult.getFirstName());
        assertEquals(user.getLastName(), userResult.getLastName());
        assertEquals(user.getEmail(), userResult.getEmail());
        assertEquals(user.getBirthDate(), userResult.getBirthDate());
        assertEquals(user.getCountry(), userResult.getCountry());
    }

    @Test
    public void testGetUserDetailsUserNotFound() throws Exception {

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            userDAO.findById("125");
        });

        when(userDAO.findById("125")).thenReturn(Optional.empty());

        assertTrue(thrown.getMessage().contains("User not found"));
    }
}
