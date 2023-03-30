package com.peppermint.restusermanager.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.peppermint.restusermanager.model.User;
import com.peppermint.restusermanager.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(25);
        user.setCountry("France");
        user.setEmail("johndoe@test.com");
        user.setPassword("testpass");

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.country", is("France")))
                .andExpect(jsonPath("$.email", is("johndoe@test.com")))
                .andExpect(jsonPath("$.password", is(nullValue())));
    }

    @Test
    public void testCreateUserWithInvalidInput() throws Exception {
        User user = new User();
        user.setFirstName("");
        user.setLastName("");
        user.setAge(17);
        user.setCountry("Spain");
        user.setEmail("invalidemail");
        user.setPassword("testpass");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("Invalid input")))
                .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                .andExpect(jsonPath("$.errors[0].message", is("First name cannot be empty")))
                .andExpect(jsonPath("$.errors[1].field", is("lastName")))
                .andExpect(jsonPath("$.errors[1].message", is("Last name cannot be empty")))
                .andExpect(jsonPath("$.errors[2].field", is("age")))
                .andExpect(jsonPath("$.errors[2].message", is("Age should be greater than or equal to 18")))
                .andExpect(jsonPath("$.errors[3].field", is("country")))
                .andExpect(jsonPath("$.errors[3].message", is("Only users living in France are allowed to register")))
                .andExpect(jsonPath("$.errors[4].field", is("email")))
                .andExpect(jsonPath("$.errors[4].message", is("Email should be valid")));
    }

    @Test
    public void testGetUser() throws Exception {
        User user = new User();
        user.setId("abc123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(25);
        user.setCountry("France");
        user.setEmail("johndoe@test.com");
        user.setPassword("testpass");

        when(userService.getUserById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.country", is("France")))
                .andExpect(jsonPath("$.email", is("johndoe@test.com")))
                .andExpect(jsonPath("$.password", is(nullValue())));
    }

    @Test
    public void testGetUserWithInvalidId() throws Exception {
        String invalidId = "invalidid";

        when(userService.getUserById(invalidId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", invalid


        