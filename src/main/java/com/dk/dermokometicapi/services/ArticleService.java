package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.*;
import com.dk.dermokometicapi.mappers.ArticleLikeMapper;
import com.dk.dermokometicapi.mappers.ArticleMapper;
import com.dk.dermokometicapi.repositories.ArticleDetailRepository;
import com.dk.dermokometicapi.repositories.ArticleLikeRepository;
import com.dk.dermokometicapi.repositories.ArticleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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

    private Page<ArticleSummaryResponseDTO> getArticleListDTO(Page<Article> articles){
        return articles.map(article -> {
            Long likes = articleRepository.findArticleLikesById(article.getId());
            Long comments = articleRepository.findArticleCommentsById(article.getId());
            return articleMapper.convertToSummaryDTO(article, likes, comments);
        });
    }

    // Regular CRUD operations

    public List<ArticleSummaryResponseDTO> getAllArticles() {
        List<Article> articles = articleRepository.getAll();
        return getArticleListDTO(articles);
    }

    @Transactional
    public void deleteById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        ArticleDetail articleDetail = article.getArticleDetail();
        articleRepository.deleteById(id);
        articleDetailRepository.delete(articleDetail);
    }

    @Transactional
    public void deleteByTitle(String title) {
        Article article = articleRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with title: " + title));
        ArticleDetail articleDetail = article.getArticleDetail();
        articleRepository.deleteByTitle(title);
        articleDetailRepository.delete(articleDetail);
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

    public Page<ArticleSummaryResponseDTO> getRecentArticles(int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findRecentArticles(PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getArticlesOrderedByLikes(int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findLikedArticles(PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getArticlesOrderedByComments(int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findCommentedArticles(PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getArticlesByType(List<String> types, int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getRecentArticlesByType(List<String> types, int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findRecentArticleByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getLikedArticlesByType(List<String> types, int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findLikedArticleByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getCommentedArticlesByType(List<String> types, int pageNum, int pageSize) {
        Page<Article> articles = articleRepository.findCommentedArticleByType(types, PageRequest.of(pageNum, pageSize));
        return getArticleListDTO(articles);
    }

    public Page<ArticleSummaryResponseDTO> getFilteredList(FilterRequestDTO filterRequestDTO){
        System.out.println(filterRequestDTO.getCategories());
        if(filterRequestDTO.getOrderBy() == null) filterRequestDTO.setOrderBy("recent");
        if(filterRequestDTO.getCategories().isEmpty()){
            return switch (filterRequestDTO.getOrderBy()) {
                case "likes" ->
                        getArticlesOrderedByLikes(filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
                case "comments" ->
                        getArticlesOrderedByComments(filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
                default -> getRecentArticles(filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
            };
        }else{
            return switch (filterRequestDTO.getOrderBy()) {
                case "likes" ->
                        getLikedArticlesByType(filterRequestDTO.getCategories(), filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
                case "comments" ->
                        getCommentedArticlesByType(filterRequestDTO.getCategories(), filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
                default -> getRecentArticlesByType(filterRequestDTO.getCategories(), filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
            };
        }
    }

    // get full article

    public ArticleResponseDTO getFullArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        Long likes = articleRepository.findArticleLikesById(id);
        Long comments = articleRepository.findArticleCommentsById(id);
        List<Long> writers = articleRepository.findWritersByArticleId(id);
        return articleMapper.convertToDTO(article, article.getArticleDetail(), likes, comments, writers);
    }

    public ArticleResponseDTO getFullArticleByTitle(String title) {
        Article article = articleRepository.findByTitle(title)
                .orElseThrow(() -> new ResolutionException("Article not found with title: " + title));
        Long likes = articleRepository.findArticleLikesById(article.getId());
        Long comments = articleRepository.findArticleCommentsById(article.getId());
        List<Long> writers = articleRepository.findWritersByArticleId(article.getId());
        return articleMapper.convertToDTO(article, article.getArticleDetail(), likes, comments, writers);
    }

    // create article

    public ArticleResponseDTO createArticle(ArticleRequestDTO article) {
        if (existsByTitle(article.getTitle())) {
            throw new BadRequestException("Article with title: " + article.getTitle() + " already exists");
        }
        List<Writer> writers = writerService.getEntities(article.getWriterIds());
        Article newArticle = articleMapper.convertToEntity(article, writers);
        newArticle.setLastUpdateDate(LocalDate.now());
        newArticle.setPublicationDate(LocalDate.now());
        ArticleDetail newArticleDetail = articleMapper.convertToDetailEntity(article, newArticle);
        articleDetailRepository.save(newArticleDetail);
        newArticle.setArticleDetail(newArticleDetail);
        articleRepository.save(newArticle);
        return articleMapper.convertToDTO(newArticle, newArticle.getArticleDetail(), 0L, 0L, article.getWriterIds());
    }

    // likes

    public ArticleLikeResponseDTO createLike(ArticleLikeRequestDTO articleLikeRequestDTO) {
        Long articleId = articleLikeRequestDTO.getArticleId();
        Long userId = articleLikeRequestDTO.getUserId();
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        User user = userService.getEntityById(userId);
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

    @Transactional
    public void deleteLike(ArticleLikeRequestDTO articleLikeRequestDTO) {
        Long articleId = articleLikeRequestDTO.getArticleId();
        Long userId = articleLikeRequestDTO.getUserId();
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        User user = userService.getEntityById(userId);
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
