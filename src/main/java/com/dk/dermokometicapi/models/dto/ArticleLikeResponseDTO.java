package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLikeResponseDTO {
    private Long id;
    private Long articleId;
    private Long userId;
    private String likeDate;
}
