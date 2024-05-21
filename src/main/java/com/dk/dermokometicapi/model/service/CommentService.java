package com.dk.dermokometicapi.model.service;


import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.entity.Article;
import com.dk.dermokometicapi.model.entity.Comment;

import com.dk.dermokometicapi.model.entity.CommentLike;
import com.dk.dermokometicapi.model.entity.User;
import com.dk.dermokometicapi.model.exception.BadRequestException;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.CommentLikeMapper;
import com.dk.dermokometicapi.model.mapper.CommentMapper;
import com.dk.dermokometicapi.model.repository.ArticleRepository;
import com.dk.dermokometicapi.model.repository.CommentLikeRepository;
import com.dk.dermokometicapi.model.repository.CommentRepository;
import com.dk.dermokometicapi.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final CommentLikeMapper commentLikeMapper;

    public CommentResponseDTO convertToDTO(Comment comment) {
        long likes = commentLikeRepository.countByComment(comment);
        long commentsCount = commentRepository.countByParentComment(comment);
        return commentMapper.convertToDTO(comment, likes, commentsCount);
    }

    // get all comments

    public List<CommentResponseDTO> getAllComments(){
        return commentRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    // get comment by id
    public CommentResponseDTO getCommentById(Long id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        long likes = commentLikeRepository.countByComment(comment);
        long commentsCount = commentRepository.countByParentComment(comment);
        return commentMapper.convertToDTO(comment, likes, commentsCount);
    }


    // create comment
    public CommentResponseDTO addComment(CommentRequestDTO commentRequestDTO){
        User user = userRepository.findById(commentRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + commentRequestDTO.getUserId()));

        Article article = articleRepository.findById(commentRequestDTO.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + commentRequestDTO.getArticleId()));

        Comment parentComment = null;

        if(commentRequestDTO.getParentCommentId() != null){
            parentComment = commentRepository.findById(commentRequestDTO.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentRequestDTO.getParentCommentId()));
        }

        Comment comment = commentMapper.convertToEntity(commentRequestDTO, article, parentComment, user);
        commentRepository.save(comment);
        return commentMapper.convertToDTO(comment, 0L, 0L);
    }

    // get comments by article id
    public Page<CommentResponseDTO> getCommentsByArticleId(Long articleId, ListRequestDTO listRequestDTO){
        Pageable pageable = Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum());
        return switch (listRequestDTO.getOrderBy()) {
            case "likes" -> commentRepository.findLikedCommentsByArticle_id(articleId, pageable)
                    .map(this::convertToDTO);
            case "comments" -> commentRepository.findCommentedCommentsByArticle_id(articleId, pageable)
                    .map(this::convertToDTO);
            default -> commentRepository.findRecentCommentsByArticle_id(articleId, pageable)
                    .map(this::convertToDTO);
        };
    }

    // get comments by parent id
    public Page<CommentResponseDTO> getCommentsByParentId(Long parentId, ListRequestDTO listRequestDTO) {
        Pageable pageable = Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum());
        return switch (listRequestDTO.getOrderBy()) {
            case "likes" -> commentRepository.findLikedCommentsByParentComment_id(parentId, pageable)
                    .map(this::convertToDTO);
            case "comments" -> commentRepository.findCommentedCommentsByParentComment_id(parentId, pageable)
                    .map(this::convertToDTO);
            default -> commentRepository.findRecentCommentsByParentComment_id(parentId, pageable)
                    .map(this::convertToDTO);
        };
    }

        // delete comment
    public void deleteComment(Long id){
        commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        commentRepository.deleteById(id);
    }

    // create like
    public CommentLikeResponseDTO addLike(CommentLikeRequestDTO commentLikeRequestDTO) {
        Comment comment = commentRepository.findById(commentLikeRequestDTO.getCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentLikeRequestDTO.getCommentId()));
        User user = userRepository.findById(commentLikeRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + commentLikeRequestDTO.getUserId()));
        if(commentLikeRepository.existsByCommentAndUser(comment, user) ){
            throw new BadRequestException("Comment like already exists with comment id: " + commentLikeRequestDTO.getCommentId() + " and user id: " + commentLikeRequestDTO.getUserId());
        }
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setPublicationDate(LocalDate.now());
        commentLikeRepository.save(commentLike);
        return commentLikeMapper.convertToDTO(commentLike);
    }

    // delete like
    public void deleteLike(Long id) {
        commentLikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment like not found with id: " + id));
        commentLikeRepository.deleteById(id);
    }

    // delete like with DTO
    public void deleteLike(CommentLikeRequestDTO commentLikeRequestDTO) {
        CommentLike commentLike = commentLikeRepository.findByComment_IdAndUser_Id(commentLikeRequestDTO.getCommentId(), commentLikeRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment like not found with comment id: " + commentLikeRequestDTO.getCommentId() + " and user id: " + commentLikeRequestDTO.getUserId()));
        commentLikeRepository.delete(commentLike);
    }
}

