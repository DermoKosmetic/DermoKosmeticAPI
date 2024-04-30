package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.dto.ArticleLikeResponseDTO;
import com.dk.dermokometicapi.model.dto.ArticleRequestDTO;
import com.dk.dermokometicapi.model.dto.ArticleResponseDTO;
import com.dk.dermokometicapi.model.dto.ArticleSummaryResponseDTO;
import com.dk.dermokometicapi.model.entity.*;
import com.dk.dermokometicapi.model.exception.BadRequestException;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.ArticleLikeMapper;
import com.dk.dermokometicapi.model.mapper.ArticleMapper;
import com.dk.dermokometicapi.model.repository.ArticleDetailRepository;
import com.dk.dermokometicapi.model.repository.ArticleLikeRepository;
import com.dk.dermokometicapi.model.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final WriterService writerService;
    private final ArticleLikeRepository articleLikeRepository;
    private final UserService userService;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleDetailRepository articleDetailRepository;

    // functions

    private List<ArticleSummaryResponseDTO> getArticleListDTO(List<Article> articles){
        List<ArticleSummaryResponseDTO> ans = new ArrayList<>();
        for (Article article : articles) {
            Long likes = articleRepository.findArticleLikesById(article.getId());
            Long comments = articleRepository.findArticleCommentsById(article.getId());
            ans.add(articleMapper.convertToSummaryDTO(article, likes, comments));
        }
        return ans;
    }

    // Regular CRUD operations

    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

    public void deleteByTitle(String title) {
        articleRepository.deleteByTitle(title);
    }

    public boolean existsByTitle(String title) {
        return articleRepository.existsByTitle(title);
    }

    public ArticleSummaryResponseDTO getByTitle(String title) {
        Article article = articleRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with title: " + title));
        Long likes = articleRepository.findArticleLikesById(article.getId());
        Long comments = articleRepository.findArticleCommentsById(article.getId());
        return articleMapper.convertToSummaryDTO(article, likes, comments);
    }

    // get filtered and/or ordered lists

    public List<ArticleSummaryResponseDTO> getAllArticlesSummary() {
        List<Article> articles = articleRepository.findAll();
        return getArticleListDTO(articles);
    }

    public List<ArticleSummaryResponseDTO> getArticlesOrderedByLikes(int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findLikedArticles(PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public List<ArticleSummaryResponseDTO> getArticlesOrderedByComments(int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findCommentedArticles(PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public List<ArticleSummaryResponseDTO> getArticlesByType(List<String> types, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public List<ArticleSummaryResponseDTO> getRecentArticlesByType(List<String> types, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findRecentArticleByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public List<ArticleSummaryResponseDTO> getLikedArticlesByType(List<String> types, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findLikedArticleByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public List<ArticleSummaryResponseDTO> getCommentedArticlesByType(List<String> types, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findCommentedArticleByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    // get full article by id

    public ArticleResponseDTO getFullArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResolutionException("Article not found with id: " + id));
        Long likes = articleRepository.findArticleLikesById(id);
        Long comments = articleRepository.findArticleCommentsById(id);
        List<Long> writers = articleRepository.findWritersByArticleId(id);
        return articleMapper.convertToDTO(article, article.getArticleDetail(), likes, comments, writers);
    }

    // create article

    public ArticleResponseDTO createArticle(ArticleRequestDTO article) {
        if (existsByTitle(article.getTitle())) {
            throw new BadRequestException("Article with title: " + article.getTitle() + " already exists");
        }
        List<Writer> writers = writerService.getEntities(article.getWriterIds());
        Article newArticle = articleMapper.convertToEntity(article, writers);
        ArticleDetail newArticleDetail = articleMapper.convertToDetailEntity(article, newArticle);
        articleRepository.save(newArticle);
        articleDetailRepository.save(newArticleDetail);
        return articleMapper.convertToDTO(newArticle, newArticle.getArticleDetail(), 0L, 0L, article.getWriterIds());
    }

    // likes

    public ArticleLikeResponseDTO createLike(Long articleId, String userName) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        if(!userService.existsByUsername(userName)){
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }
        User user = userService.getEntityByUsername(userName);
        if(articleLikeRepository.existsByArticleAndUser(article, user)){
            throw new BadRequestException("User already liked this article");
        }
        ArticleLike newLike = new ArticleLike();
        newLike.setArticle(article);
        newLike.setUser(user);
        newLike.setLikeDate(LocalDate.now());
        articleLikeRepository.save(newLike);
        return articleLikeMapper.convertToResponseDTO(newLike);
    }

    public void deleteLike(Long articleId, String userName) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        if (!userService.existsByUsername(userName)) {
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }
        User user = userService.getEntityByUsername(userName);
        if (!articleLikeRepository.existsByArticleAndUser(article, user)) {
            throw new BadRequestException("User did not like this article");
        }
        articleLikeRepository.deleteByArticleAndUser(article, user);
    }

    // update article

    public ArticleResponseDTO updateArticle(Long id, ArticleRequestDTO articleRequestDTO) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        ArticleDetail articleDetail = articleDetailRepository.findById(article.getArticleDetail().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Article detail not found with id: " + article.getArticleDetail().getId()));
        List<Writer> writers = writerService.getEntities(articleRequestDTO.getWriterIds());
        article.setTitle(articleRequestDTO.getTitle());
        article.setDescription(articleRequestDTO.getDescription());
        article.setType(articleRequestDTO.getType());
        article.setMainImg(articleRequestDTO.getMainImg());
        article.setWriters(writers);
        article.setLastUpdateDate(LocalDate.now());
        articleRepository.save(article);

        articleDetail.setContent(articleRequestDTO.getContent());
        articleDetailRepository.save(articleDetail);

        return articleMapper.convertToDTO(article, articleDetail, articleRepository.findArticleLikesById(article.getId()), articleRepository.findArticleLikesById(article.getId()), articleRequestDTO.getWriterIds());
    }
}
