package com.peppermint.restusermanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.anyOf;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peppermint.restusermanager.dao.IUserDAO;
import com.peppermint.restusermanager.dto.UserCreationDto;
import com.peppermint.restusermanager.dto.UserDto;
import com.peppermint.restusermanager.exceptions.NotFoundException;
import com.peppermint.restusermanager.model.User;
import com.peppermint.restusermanager.service.UserService;



@SpringBootTest
@DisplayName("User controller validation test")
@AutoConfigureMockMvc
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private IUserDAO userDAO;

        @Mock
        private Logger mockLogger;

        @Captor
        private ArgumentCaptor<String> stringCaptor;

        @Test
        public void testfindall() throws Exception {

                String jsonList = "["
                                + "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"johndoe@example.com\", \"birthDate\":\"2003-04-29\", \"country\":\"France\"},"
                                + "{\"firstName\":\"Johnny\", \"lastName\":\"Doerty\", \"email\":\"Johnny@example.com\", \"birthDate\":\"2002-07-19\", \"country\":\"France\"},"
                                + "{\"firstName\":\"Johnovan\", \"lastName\":\"Doenica\", \"email\":\"Johnovan@example.com\", \"birthDate\":\"2001-06-18\", \"country\":\"France\"},"
                                + "{\"firstName\":\"Johnovic\", \"lastName\":\"Doenicole\", \"email\":\"Johnovic@example.com\", \"birthDate\":\"2000-06-11\", \"country\":\"France\"},"
                                + "{\"firstName\":\"Johnallo\", \"lastName\":\"Doel\", \"email\":\"Johnallo@example.com\", \"birthDate\":\"1999-04-12\", \"country\":\"France\"}"
                                + "]";

                ObjectMapper jsonMapper = new ObjectMapper();
                List<UserDto> usersDtos = jsonMapper.readValue(jsonList,
                                new TypeReference<List<UserDto>>() {});

                when(userService.getAllUsers()).thenReturn(usersDtos);
                mockMvc.perform(get("/api/users/list-users").contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(usersDtos))).andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(5)))
                                .andExpect(jsonPath("$.[*].firstName").isNotEmpty());
        }

        @Test
        public void testRegisterUser() throws Exception {

                UserCreationDto userCreationDto = new UserCreationDto("John", "Doe",
                                "johndoe@example.com", LocalDate.now().minusYears(20), "France",
                                "password");
                UserDto userDto = new UserDto(userCreationDto.getFirstName(),
                                userCreationDto.getLastName(), userCreationDto.getEmail(),
                                userCreationDto.getBirthDate(), userCreationDto.getCountry());

                when(userService.registerUser(any(UserCreationDto.class))).thenReturn(userDto);
                when(userService.isValidAgeAndCountry(userCreationDto.getBirthDate(),
                                userCreationDto.getCountry())).thenReturn(true);

                mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(userDto))).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.firstName", is("John")))
                                .andExpect(jsonPath("$.lastName", is("Doe")))
                                .andExpect(jsonPath("$.birthDate",
                                                is(userDto.getBirthDate().toString())))
                                .andExpect(jsonPath("$.country", is("France")))
                                .andExpect(jsonPath("$.email", is("johndoe@example.com")));
        }

        @Test
        public void testCreateUserWithInvalidInput() throws Exception {
                UserCreationDto userCreationDto = new UserCreationDto("", "", "invalidemail",
                                LocalDate.now().minusYears(17), "Spain", "testpass");

                StringWriter writer = new StringWriter();

                mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(userCreationDto)))
                                .andExpect(status().isBadRequest())
                                .andDo(MockMvcResultHandlers.print(writer))
                                .andExpect(jsonPath("$.errors[0]",
                                                anyOf(is("First name is mandatory"),
                                                                is("Valid email is mandatory"))))
                                .andExpect(jsonPath("$.errors[1]",
                                                anyOf(is("First name is mandatory"),
                                                                is("Valid email is mandatory"))));
        }

        @Test
        public void testCreateUserWithAge() throws Exception {
                UserCreationDto userCreationDto = new UserCreationDto("John", "Doe",
                                "johndoe@example.com", LocalDate.now().minusYears(17), "Spain",
                                "testpass");

                StringWriter writer = new StringWriter();

                mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(userCreationDto)))
                                .andExpect(status().isBadRequest())
                                .andDo(MockMvcResultHandlers.print(writer))
                                .andExpect(jsonPath("$.errors[0]", is(
                                                "User must be over 18 years old and living in France to register.")));
        }

        @Test
        public void testGetUserByID() throws Exception {
                User user = new User("abc123", "John", "Doe", "johndoe@test.com", "testpass2",
                                "France", LocalDate.now().minusYears(25));

                when(userDAO.save(user)).thenReturn(user);
                UserDto userDto = new UserDto(user.getFirstName(), user.getLastName(),
                                user.getEmail(), user.getBirthDate(), user.getCountry());

                when(userService.getUserById(user.getId())).thenReturn(userDto);

                mockMvc.perform(get("/api/users/{id}", user.getId())).andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName", is("John")))
                                .andExpect(jsonPath("$.lastName", is("Doe")))
                                .andExpect(jsonPath("$.birthDate",
                                                is(userDto.getBirthDate().toString())))
                                .andExpect(jsonPath("$.country", is("France")))
                                .andExpect(jsonPath("$.email", is("johndoe@test.com")));
        }

    @Test
    public void testGetUserWithInvalidId() throws Exception {
        when(userService.getUserById("invalid_id")).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/users/{id}", "invalid_id")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found with id: invalid_id")))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())));
    }

        public static String asJsonString(final Object obj) {
                try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.findAndRegisterModules();
                        return mapper.writeValueAsString(obj);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}
