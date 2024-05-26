package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.CommentLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.CommentLikeResponseDTO;
import com.dk.dermokometicapi.models.entity.CommentLike;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentLikeMapper {
    private final ModelMapper modelMapper;

    public CommentLike convertToEntity(CommentLikeRequestDTO commentLikeRequestDTO) {
        return modelMapper.map(commentLikeRequestDTO, CommentLike.class);
    }

    public CommentLikeResponseDTO convertToDTO(CommentLike commentLike) {
        return modelMapper.map(commentLike, CommentLikeResponseDTO.class);
    }
}
