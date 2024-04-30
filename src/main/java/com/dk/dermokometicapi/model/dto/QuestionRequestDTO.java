package com.dk.dermokometicapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    @Size(min = 10, max = 200, message = "Content must be between 10 and 200 characters")
    private String content;
    @NotBlank(message = "Publication Date is mandatory")

    @NotBlank(message = "Publication Date is mandatory")
    private String publicationDate;

    @NotBlank(message = "User Id is mandatory")
    private Long userId;
}
