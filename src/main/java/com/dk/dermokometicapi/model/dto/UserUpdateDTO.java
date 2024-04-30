package com.dk.dermokometicapi.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Email
    @Nullable
    private String email;
    @URL
    @Nullable
    private String profilePic;
}
