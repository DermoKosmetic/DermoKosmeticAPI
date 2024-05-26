package com.dk.dermokometicapi.models.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLikeRequestDTO {
    @NotNull(message = "Article Id is mandatory")
    private Long articleId;
    @NotNull(message = "User Id is mandatory")
    private Long userId;
}
