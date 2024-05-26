package com.dk.dermokometicapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeResponseDTO {
    private Long id;
    private Long commentId;
    private Long userId;
    private String publicationDate;
}
