package com.dk.dermokometicapi.models.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    @Nullable
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @Nullable
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @Size(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    @Email(message = "Email must be a valid email")
    @Nullable
    private String email;

    @URL(message = "Profile Picture must be a valid URL")
    @Nullable
    private String profilePic;
}
