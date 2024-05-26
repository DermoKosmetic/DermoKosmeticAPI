package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionLikeResponseDTO {
    private Long id;
    private Long questionId;
    private Long userId;
    private String likeDate;
}
