package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.CommentRequestDTO;
import com.dk.dermokometicapi.model.dto.CommentResponseDTO;
import com.dk.dermokometicapi.model.entity.Article;
import com.dk.dermokometicapi.model.entity.Comment;
import com.dk.dermokometicapi.model.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class CommentMapper {
    private final ModelMapper modelMapper;

    public Comment convertToEntity(CommentRequestDTO commentRequestDTO, Article article, Comment parentComment, User user) {
        Comment comment = new Comment();
        comment.setContent(commentRequestDTO.getContent());
        comment.setArticle(article);
        comment.setParentComment(parentComment);
        comment.setUser(user);
        comment.setPublicationDate(LocalDate.now());
        return comment;
    }

    public CommentResponseDTO convertToDTO(Comment comment, Long likes, Long responses) {
        CommentResponseDTO commentResponseDTO = modelMapper.map(comment, CommentResponseDTO.class);
        commentResponseDTO.setLikeNumber(likes);
        commentResponseDTO.setResponseNumber(responses);
        return commentResponseDTO;
    }

}
