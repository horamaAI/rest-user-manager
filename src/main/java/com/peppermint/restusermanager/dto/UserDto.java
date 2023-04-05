package com.peppermint.restusermanager.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// only the information relevant for the client, for example password and age is not needed by the
// client
@AllArgsConstructor
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String country;

}
