package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.services.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleSummaryResponseDTO>> getAllArticles() {
        return new ResponseEntity<>(articleService.getAllArticles(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ArticleResponseDTO> getFullArticleById(@PathVariable Long id) {
        return new ResponseEntity<>(articleService.getFullArticleById(id), HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<ArticleResponseDTO> getFullArticleByTitle(@PathVariable String title) {
        return new ResponseEntity<>(articleService.getFullArticleByTitle(title), HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<ArticleSummaryResponseDTO>> searchArticles(@RequestBody FilterRequestDTO filterRequestDTO) {
        return new ResponseEntity<>(articleService.getFilteredList(filterRequestDTO), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ArticleResponseDTO> createArticle(@RequestBody ArticleRequestDTO articleRequestDTO) {
        return new ResponseEntity<>(articleService.createArticle(articleRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/like")
    public ResponseEntity<ArticleLikeResponseDTO> likeArticle(@RequestBody ArticleLikeRequestDTO articleLikeRequestDTO) {
        return new ResponseEntity<>(articleService.createLike(articleLikeRequestDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id) {
        articleService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> deleteLike(@RequestParam Long articleId, @RequestParam Long userId) {
        articleService.deleteLike(articleId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/like")
    public ResponseEntity<Boolean> getLike(@RequestParam Long articleId, @RequestParam Long userId) {
        return new ResponseEntity<>(articleService.getLike(articleId, userId), HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getDistinctTypes() {
        return new ResponseEntity<>(articleService.getTypes(), HttpStatus.OK);
    }

}
