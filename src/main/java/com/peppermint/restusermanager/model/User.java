package com.peppermint.restusermanager.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@Data
@NoArgsConstructor
@Setter
@Getter
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Object password;
    private LocalDate birthDate;
    private String country;

    public User(String firstName, String lastName, String email, String password,
            LocalDate birthDate, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = encrypt(password);
        this.birthDate = birthDate;
        this.country = country;
    }

    private Object encrypt(String password) {
        return this.password = password;
    }

    // Add getters and setters

}
