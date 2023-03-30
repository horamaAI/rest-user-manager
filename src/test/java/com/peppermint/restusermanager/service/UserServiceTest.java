package com.peppermint.restusermanager.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.peppermint.restusermanager.dao.UserRepository;
import com.peppermint.restusermanager.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testRegisterUserSuccess() throws Exception {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setAge(25);
        user.setCountry("France");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.registerUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getAge(), result.getAge());
        assertEquals(user.getCountry(), result.getCountry());
    }

    @Test(expected = InvalidUserException.class)
    public void testRegisterUserInvalidAge() throws Exception {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setAge(15);
        user.setCountry("France");

        // Act
        userService.registerUser(user);

        // Assert
        // InvalidUserException should be thrown
    }

    @Test(expected = InvalidUserException.class)
    public void testRegisterUserInvalidCountry() throws Exception {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setAge(25);
        user.setCountry("USA");

        // Act
        userService.registerUser(user);

        // Assert
        // InvalidUserException should be thrown
    }

    @Test
    public void testGetUserDetailsSuccess() throws Exception {
        // Arrange
        User user = new User();
        user.setId("123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setAge(25);
        user.setCountry("France");

        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserDetails("123");

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getAge(), result.getAge());
        assertEquals(user.getCountry(), result.getCountry());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserDetailsUserNotFound() throws Exception {
        // Arrange
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        // Act
        userService.getUserDetails("123");

        // Assert
        // UserNotFoundException should be thrown
    }
}
