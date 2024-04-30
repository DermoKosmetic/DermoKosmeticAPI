package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.CommentRequestDTO;
import com.dk.dermokometicapi.model.dto.CommentResponseDTO;
import com.dk.dermokometicapi.model.entity.Comment;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentMapper {
    private final ModelMapper modelMapper;

    public Comment convertToEntity(CommentRequestDTO commentRequestDTO) {
        return modelMapper.map(commentRequestDTO, Comment.class);
    }

    public CommentResponseDTO convertToDTO(Comment comment, Long likes, Long responses) {
        CommentResponseDTO commentResponseDTO = modelMapper.map(comment, CommentResponseDTO.class);
        commentResponseDTO.setLikes(likes);
        commentResponseDTO.setResponses(responses);
        return commentResponseDTO;
    }
}
