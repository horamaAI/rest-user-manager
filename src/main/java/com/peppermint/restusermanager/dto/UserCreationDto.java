package com.peppermint.restusermanager.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
// all the data necessary to create a user and send it to the server in a single request, which
// optimizes the interactions with the API:
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDto {

    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String country;
    private String password;
    // private List<String> roles;

}
