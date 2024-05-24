package com.dk.dermokometicapi.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    @NotBlank(message = "Content is mandatory")
    @Size(min = 1, max = 200, message = "Content must be between 1 and 200 characters")
    private String content;

    private Long parentCommentId;

    @NotNull(message = "Article Id is mandatory")
    private Long articleId;

    @NotNull(message = "User Id is mandatory")
    private Long userId;
}
