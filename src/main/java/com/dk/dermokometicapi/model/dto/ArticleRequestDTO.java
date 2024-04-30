package com.dk.dermokometicapi.model.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequestDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(min = 5, max = 50, message = "Title must be between 5 and 50 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 5, max = 100, message = "Description must be between 5 and 100 characters")
    private String description;

    @NotBlank(message = "Type is mandatory")
    @Size(min = 1, max = 20, message = "Type must be between 1 and 20 characters")
    private String type;

    @URL(message = "Main Image must be a valid URL")
    private String mainImg;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotEmpty(message = "Writer Ids are mandatory")
    private List<Long> writerIds;

}
