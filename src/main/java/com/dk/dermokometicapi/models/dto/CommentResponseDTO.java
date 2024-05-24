package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long likeNumber;
    private Long responseNumber;
}
