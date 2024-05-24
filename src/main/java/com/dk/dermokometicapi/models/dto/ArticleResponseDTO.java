package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String mainImg;
    private String publicationDate;
    private String lastUpdateDate;
    private Long likes;
    private Long comments;
    private List<Long> writerIds;
    private String content;
}
