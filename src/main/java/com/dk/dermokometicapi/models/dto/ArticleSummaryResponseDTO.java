package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSummaryResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String mainImg;
    private String publicationDate;
    private String lastUpdateDate;
    private Long likes;
    private Long comments;
}
