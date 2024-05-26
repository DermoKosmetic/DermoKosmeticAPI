package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String publicationDate;
    private String type;
    private Long userId;
    private Long likes;
    private Long answers;
}
