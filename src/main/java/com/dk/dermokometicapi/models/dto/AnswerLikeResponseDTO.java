package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerLikeResponseDTO {
    private Long id;
    private Long answerId;
    private Long userId;
    private String likeDate;
}
