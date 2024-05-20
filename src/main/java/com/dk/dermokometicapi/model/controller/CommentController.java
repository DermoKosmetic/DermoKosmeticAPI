package com.dk.dermokometicapi.model.controller;

import com.dk.dermokometicapi.model.dto.CommentResponseDTO;
import com.dk.dermokometicapi.model.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //comentarios por id de articulo
    //http://localhost:8080/api/v2/comments/anticle/2454435
    @GetMapping("/article/{articleId}") //cambiar long por string
    public ResponseEntity<List<CommentResponseDTO>> getCommentsbyArticleId (@PathVariable Long articleId){
        List<CommentResponseDTO> comments = commentService.getCommentsbyArticleId(articleId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

}
