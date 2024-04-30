package com.dk.dermokometicapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String publicationDate;
    private Long parentCommentId;
    private Long articleId;
    private Long userId;
    private Long likes;
    private Long responses;
}
