package com.peppermint.restusermanager.controller;

import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.CoreMatchers.is;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
// import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peppermint.restusermanager.dao.IUserDAO;
import com.peppermint.restusermanager.dto.UserCreationDto;
import com.peppermint.restusermanager.dto.UserDto;
import com.peppermint.restusermanager.exceptions.NotFoundException;
import com.peppermint.restusermanager.model.User;
import com.peppermint.restusermanager.service.UserService;

// @RunWith(SpringRunner.class)
@SpringBootTest
@DisplayName("User controller validation test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private IUserDAO userDAO;

    @Test
    public void testRegisterUser() throws Exception {

        UserCreationDto userCreationDto = new UserCreationDto("John", "Doe", "johndoe@example.com",
                LocalDate.now().minusYears(20), "password", "France");
        // User user = new User(userCreationDto.getFirstName(), userCreationDto.getLastName(),
        // userCreationDto.getEmail(), userCreationDto.getPassword(),
        // userCreationDto.getBirthDate(), userCreationDto.getCountry());

        // UserDto resultDto = userService.registerUser(userCreationDto);
        UserDto userDto = new UserDto(userCreationDto.getFirstName(), userCreationDto.getLastName(),
                userCreationDto.getEmail(), userCreationDto.getBirthDate(),
                userCreationDto.getCountry());

        when(userService.registerUser(userCreationDto)).thenReturn(userDto);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe"))).andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.country", is("France")))
                .andExpect(jsonPath("$.email", is("johndoe@example.com")))
                .andExpect(jsonPath("$.password", is(nullValue())));
    }

    @Test
    public void testCreateUserWithInvalidInput() throws Exception {
        User user = new User();
        user.setFirstName("");
        user.setLastName("");
        user.setBirthDate(LocalDate.now().minusYears(17));
        user.setCountry("Spain");
        user.setEmail("invalidemail");
        user.setPassword("testpass");

        mockMvc.perform(
                post("/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("Invalid input")))
                .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                .andExpect(jsonPath("$.errors[0].message", is("First name cannot be empty")))
                .andExpect(jsonPath("$.errors[1].field", is("lastName")))
                .andExpect(jsonPath("$.errors[1].message", is("Last name cannot be empty")))
                .andExpect(jsonPath("$.errors[2].field", is("age")))
                .andExpect(jsonPath("$.errors[2].message",
                        is("Age should be greater than or equal to 18")))
                .andExpect(jsonPath("$.errors[3].field", is("country")))
                .andExpect(jsonPath("$.errors[3].message",
                        is("Only users living in France are allowed to register")))
                .andExpect(jsonPath("$.errors[4].field", is("email")))
                .andExpect(jsonPath("$.errors[4].message", is("Email should be valid")));
    }

    @Test
    public void testGetUser() throws Exception {
        User user = new User();
        user.setId("abc123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setCountry("France");
        user.setEmail("johndoe@test.com");
        user.setPassword("testpass");

        when(userDAO.save(user)).thenReturn(user);
        UserDto userDto = new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getBirthDate(), user.getCountry());

        when(userService.getUserById(user.getId())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", user.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe"))).andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.country", is("France")))
                .andExpect(jsonPath("$.email", is("johndoe@test.com")))
                .andExpect(jsonPath("$.password", is(nullValue())));
    }

    @Test
    public void testGetUserWithInvalidId() throws Exception {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            userDAO.findById("invalid_id");
        });

        assertTrue(thrown.getMessage().contains("User not found"));
        when(userDAO.findById("invalid_id")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", "invalid_id")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.getReasonPhrase())));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
