package com.dk.dermokometicapi.model.controller;

import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ArticleResponseDTO> getFullArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getFullArticleById(id));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<ArticleResponseDTO> getFullArticleByTitle(@PathVariable String title) {
        return ResponseEntity.ok(articleService.getFullArticleByTitle(title));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ArticleSummaryResponseDTO>> searchArticles(@RequestBody FilterRequestDTO filterRequestDTO) {
        return ResponseEntity.ok(articleService.getFilteredList(filterRequestDTO));
    }

    @PostMapping
    public ResponseEntity<ArticleResponseDTO> createArticle(@RequestBody ArticleRequestDTO articleRequestDTO) {
        return ResponseEntity.ok(articleService.createArticle(articleRequestDTO));
    }

    @PostMapping("/like")
    public ResponseEntity<ArticleLikeResponseDTO> likeArticle(@RequestBody ArticleLikeRequestDTO articleLikeRequestDTO) {
        return ResponseEntity.ok(articleService.createLike(articleLikeRequestDTO));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id) {
        articleService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> deleteLike(@RequestBody ArticleLikeRequestDTO articleLikeRequestDTO) {
        articleService.deleteLike(articleLikeRequestDTO);
        return ResponseEntity.ok().build();
    }


}
