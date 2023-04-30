
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private IUserDAO userDAO;

    @Test
    public void testRegisterUserSuccess() throws Exception {

        UserCreationDto userCreationDto = new UserCreationDto("John", "Doe", "johndoe@example.com",
                LocalDate.now().minusYears(25), "France", "password");

        User user = new User(userCreationDto.getFirstName(), userCreationDto.getLastName(),
                userCreationDto.getEmail(), userCreationDto.getPassword(),
                userCreationDto.getCountry(), userCreationDto.getBirthDate());

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
        User user = new User("123", "John", "Doe", "john.doe@test.com", "yolo", "France",
                LocalDate.now().minusYears(25));


        when(userDAO.save(user)).thenReturn(user);

        // Act
        userService.save(user);
        String email = user.getEmail();
        when(userDAO.findByEmail(email)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getByEmail(email);

        // Assert
        assertTrue(result.isPresent());
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
        when(userDAO.findById("125")).thenReturn(Optional.empty());
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUserById("125");
        });

        assertTrue(thrown.getMessage().contains("User not found"));
    }
}
