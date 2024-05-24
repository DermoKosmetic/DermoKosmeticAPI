package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {
    private Long id;
    private String content;
    private String publicationDate;
    private Long parentAnswerId;
    private Long questionId;
    private Long userId;
    private Long likes;
    private Long responses;
}
