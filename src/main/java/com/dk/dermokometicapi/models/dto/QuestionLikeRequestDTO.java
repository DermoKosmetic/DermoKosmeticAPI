package com.dk.dermokometicapi.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionLikeRequestDTO {
    @NotBlank(message = "Question Id is mandatory")
    private Long questionId;
    @NotBlank(message = "User Id is mandatory")
    private Long userId;
}
