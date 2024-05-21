package com.dk.dermokometicapi.model.controller;


import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // get all comments
    @GetMapping()
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(){
        List<CommentResponseDTO> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // get comment by id
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id){
        CommentResponseDTO comment = commentService.getCommentById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    // create comment
    @PostMapping()
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody @Valid CommentRequestDTO commentRequestDTO){
        CommentResponseDTO comment = commentService.addComment(commentRequestDTO);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // get comments by article id
    @GetMapping("/article/{id}")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByArticleId(@PathVariable Long id, @RequestBody @Valid ListRequestDTO listRequestDTO){
        Page<CommentResponseDTO> comments = commentService.getCommentsByArticleId(id, listRequestDTO);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // get comments by parent id
    @GetMapping("/parent/{id}")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByParentId(@PathVariable Long id, @RequestBody @Valid ListRequestDTO listRequestDTO){
        Page<CommentResponseDTO> comments = commentService.getCommentsByParentId(id, listRequestDTO);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id){
        commentService.deleteComment(id);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

    // like comment
    @PostMapping("/like")
    public ResponseEntity<CommentLikeResponseDTO> likeComment(@RequestBody @Valid CommentLikeRequestDTO commentLikeRequestDTO){
        return new ResponseEntity<>(commentService.addLike(commentLikeRequestDTO), HttpStatus.OK);
    }

    // unlike comment
    @DeleteMapping("/like/{id}")
    public ResponseEntity<String> unlikeComment(@PathVariable Long id){
        commentService.deleteLike(id);
        return new ResponseEntity<>("Comment unliked successfully", HttpStatus.OK);
    }

    // delete like by comment id and user id
    @DeleteMapping("/like")
    public ResponseEntity<String> deleteLikeByCommentIdAndUserId(@RequestBody @Valid CommentLikeRequestDTO commentLikeRequestDTO){
        commentService.deleteLike(commentLikeRequestDTO);
        return new ResponseEntity<>("Comment unliked successfully", HttpStatus.OK);
    }
}
