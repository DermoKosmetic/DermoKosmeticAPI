package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.dto.CommentRequestDTO;
import com.dk.dermokometicapi.model.dto.CommentResponseDTO;
import com.dk.dermokometicapi.model.entity.Article;
import com.dk.dermokometicapi.model.entity.Comment;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.CommentMapper;
import com.dk.dermokometicapi.model.repository.ArticleRepository;
import com.dk.dermokometicapi.model.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;

    //agregr un comentario

    public CommentResponseDTO addComment(Long articleId, CommentRequestDTO commentRequestDTO){
        Comment comment = commentMapper.convertToEntity(commentRequestDTO);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with article id: " + articleId));
        comment.setArticle(article);
        commentRepository.save(comment);
        return commentMapper.convertToDTO(comment);    //falta likes y comentarios
    }

    //agregar respuesta a un comentario
    public CommentResponseDTO addReply(Long commentId, CommentRequestDTO commentRequestDTO){
        Comment comment = commentMapper.convertToEntity(commentRequestDTO);
        Comment commentReply = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with article id: " + commentId));

        comment.setParentComment(commentReply);
        commentRepository.save(comment);
        return commentMapper.convertToDTO(comment);    //falta likes y comentarios
    }

    //obtenr comentarios por id de articulo
    public List<CommentResponseDTO> getCommentsbyArticleId(Long articleId){
        List<Comment> comments = commentRepository.getByArticle_Id(articleId);
        return commentMapper.convertToListDTO(comments);
    }


}
