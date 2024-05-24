package com.dk.dermokometicapi.models.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerLikeRequestDTO {
    @NotNull(message = "Answer Id is mandatory")
    private Long answerId;
    @NotNull(message = "User Id is mandatory")
    private Long userId;
}
