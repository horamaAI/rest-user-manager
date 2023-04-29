package com.peppermint.restusermanager.dto;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String country;
    private String password;
    // private List<String> roles;

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "UserCreationDto:{firstName:'" + firstName + "', lastName:'" + lastName
                + "', email:'" + email + "', birthDate:'" + birthDate.toString() + "', country:'"
                + country + "', password:'" + password + "'}";
    }

}
