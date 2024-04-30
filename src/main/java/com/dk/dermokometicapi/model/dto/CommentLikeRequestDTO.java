package com.dk.dermokometicapi.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeRequestDTO {
    @NotNull(message = "Comment Id is mandatory")
    private Long commentId;
    @NotNull(message = "User Id is mandatory")
    private Long userId;
}
