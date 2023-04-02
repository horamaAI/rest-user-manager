package com.peppermint.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
// all the data necessary to create a user and send it to the server in a single request, which
// optimizes the interactions with the API:
public class UserCreationDto {

    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String country;
    private String password;
    // private List<String> roles;

}
