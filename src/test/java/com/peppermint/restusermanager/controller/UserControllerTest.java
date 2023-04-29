package com.peppermint.restusermanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
// import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peppermint.restusermanager.dao.IUserDAO;
import com.peppermint.restusermanager.dto.UserCreationDto;
import com.peppermint.restusermanager.dto.UserDto;
import com.peppermint.restusermanager.exceptions.NotFoundException;
import com.peppermint.restusermanager.model.User;
import com.peppermint.restusermanager.service.UserService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
// import ch.qos.logback.classic.Logger;
// import ch.qos.logback.classic.spi.LoggingEvent;

// @RunWith(SpringRunner.class)
@SpringBootTest
@DisplayName("User controller validation test")
@AutoConfigureMockMvc
// @ExtendWith(MockitoExtension.class)
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
        List<UserDto> usersDtos =
                jsonMapper.readValue(jsonList, new TypeReference<List<UserDto>>() {});
        System.out.println("xoxo users size " + usersDtos.size());
        for (UserDto userDto : usersDtos) {
            System.out.println("xoxo user " + userDto.getFirstName());
        }
        when(userService.getAllUsers()).thenReturn(usersDtos);
        mockMvc.perform(get("/api/users/list-users").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usersDtos))).andExpect(status().isOk())
                // .assertEquals(usersDtos.size(), 5)
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$.[*].firstName").isNotEmpty());
    }

    @Test
    public void testRegisterUser() throws Exception {

        UserCreationDto userCreationDto = new UserCreationDto("John", "Doe", "johndoe@example.com",
                LocalDate.now().minusYears(20), "France", "password");
        // User user = new User(userCreationDto.getFirstName(),
        // userCreationDto.getLastName(),
        // userCreationDto.getEmail(), userCreationDto.getPassword(),
        // userCreationDto.getBirthDate(), userCreationDto.getCountry());

        UserDto userDto = new UserDto(userCreationDto.getFirstName(), userCreationDto.getLastName(),
                userCreationDto.getEmail(), userCreationDto.getBirthDate(),
                userCreationDto.getCountry());

        // Logger mockLogger = LoggerFactory.getLogger(UserController.class);

        when(userService.registerUser(any(UserCreationDto.class))).thenReturn(userDto);
        System.out.println("");
        // userDto = userService.registerUser(userCreationDto);

        // Mockito.verify(mockLogger).info(stringCaptor.capture());
        // String logMsg = stringCaptor.getValue();
        // assertEquals("Registered user with email: johndoe@example.com", logMsg);

        when(userService.isValidAgeAndCountry(userCreationDto.getBirthDate(),
                userCreationDto.getCountry())).thenReturn(true);

        // Mockito.verify(mockAppender).doAppend(captorLoggingEvent.capture());
        // verify(mockLogger).info("Registered user with email: {}", userDto.getEmail());
        // Mockito.verify(mockLogger).info("Registered user with email: {}", userDto.getEmail());

        // Having a genricised captor means we don't need to cast
        // final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        // Check log level is correct
        // assertEquals(Level.INFO, loggingEvent.getLevel());
        // String astr = "Registered user with email: {}" + userDto.getEmail();
        // Check the message being logged is correct
        // assertEquals(loggingEvent.getFormattedMessage(), astr);
        System.out.println("xoxo userCreationDto: " + userCreationDto.toString());
        System.out.println("xoxo userDto: " + userDto.toString());
        System.out.println("xoxo dao is null ? : " + (userService == null));

        String res = asJsonString(userDto);
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode jsonNode = jsonMapper.readTree(res);

        Iterator<String> iterator = jsonNode.fieldNames();
        List<String> keys = new ArrayList<>();
        iterator.forEachRemaining(e -> keys.add(e));

        System.out.println("xoxo 123 print asJsonString result " + jsonNode.toString());
        System.out.println("xoxo test test 123");
        System.out.println("xoxo 123 keys: " + keys.toString());

        mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.birthDate", is("2003-04-29")))
                .andExpect(jsonPath("$.country", is("France")))
                .andExpect(jsonPath("$.email", is("johndoe@example.com")));
    }

    // @Test
    // public void testCreateUserWithInvalidInput() throws Exception {
    // User user = new User();
    // user.setFirstName("");
    // user.setLastName("");
    // user.setBirthDate(LocalDate.now().minusYears(17));
    // user.setCountry("Spain");
    // user.setEmail("invalidemail");
    // user.setPassword("testpass");

    // mockMvc.perform(
    // post("/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(user)))
    // .andExpect(status().isBadRequest())
    // .andExpect(jsonPath("$.errorMessage", is("Invalid input")))
    // .andExpect(jsonPath("$.errors[0].field", is("firstName")))
    // .andExpect(jsonPath("$.errors[0].message", is("First name cannot be empty")))
    // .andExpect(jsonPath("$.errors[1].field", is("lastName")))
    // .andExpect(jsonPath("$.errors[1].message", is("Last name cannot be empty")))
    // .andExpect(jsonPath("$.errors[2].field", is("age")))
    // .andExpect(jsonPath("$.errors[2].message",
    // is("Age should be greater than or equal to 18")))
    // .andExpect(jsonPath("$.errors[3].field", is("country")))
    // .andExpect(jsonPath("$.errors[3].message",
    // is("Only users living in France are allowed to register")))
    // .andExpect(jsonPath("$.errors[4].field", is("email")))
    // .andExpect(jsonPath("$.errors[4].message", is("Email should be valid")));
    // }

    @Test
    public void testGetUser() throws Exception {
        User user = new User();
        user.setId("abc123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setCountry("France");
        user.setEmail("johndoe@test.com");
        user.setPassword("testpass2");

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
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            String ret = mapper.writeValueAsString(obj);
            System.out.println("xoxo string    : " + mapper.writeValueAsString(obj));
            System.out.println("xoxo stringjson: " + ret);
            // System.out.println("country : " + ret.substring(18, 33));
            // String country = obj.getCountry();
            // boolean isInFrance = country.equals("France");
            // LocalDate todayDate = LocalDate.now();
            // System.out.println("knowing today's date is" + todayDate.toString());
            // int age = Period.between(obj.getBirthDate(), todayDate).getYears();
            // System.out.println("test1: age is okay ?: " + (age >= 18));
            // System.out.println("test2: country is okay ?: " + isInFrance);

            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
