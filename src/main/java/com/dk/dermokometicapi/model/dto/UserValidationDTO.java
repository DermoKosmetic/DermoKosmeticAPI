package com.dk.dermokometicapi.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationDTO {
    @Nullable
    private String username;

    @NotBlank
    private String password;

    @Email
    @Nullable
    private String email;
}
