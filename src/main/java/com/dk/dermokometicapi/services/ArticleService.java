package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.*;
import com.dk.dermokometicapi.mappers.ArticleLikeMapper;
import com.dk.dermokometicapi.mappers.ArticleMapper;
import com.dk.dermokometicapi.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    // functions

    private ArticleSummaryResponseDTO getSummaryDTO(Article article){
        Long likes = articleLikeRepository.countByArticle(article);
        Long comments = commentRepository.countByArticle(article);
        return articleMapper.convertToSummaryDTO(article, likes, comments);
    }

    private ArticleResponseDTO getFullDTO(Article article){
        Long likes = articleLikeRepository.countByArticle(article);
        Long comments = commentRepository.countByArticle(article);
        List<Long> ids = article.getWriters().stream().map(Writer::getId).toList();
        return articleMapper.convertToDTO(article, article.getArticleDetail(), likes, comments, ids);
    }

    // Regular CRUD operations

    public List<ArticleSummaryResponseDTO> getAllArticles() {
        return articleRepository.getAll().stream().map(this::getSummaryDTO).toList();
    }

    @Transactional
    public void deleteById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        ArticleDetail articleDetail = article.getArticleDetail();

        commentLikeRepository.deleteByComment_Article(article);
        commentRepository.deleteByArticle(article);
        articleLikeRepository.deleteByArticle(article);
        articleRepository.deleteById(id);
        articleDetailRepository.delete(articleDetail);
    }

    @Transactional
    public void deleteByTitle(String title) {
        Article article = articleRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with title: " + title));
        ArticleDetail articleDetail = article.getArticleDetail();

        commentLikeRepository.deleteByComment_Article(article);
        commentRepository.deleteByArticle(article);
        articleLikeRepository.deleteByArticle(article);
        articleRepository.deleteByTitle(title);
        articleDetailRepository.delete(articleDetail);
    }

    public boolean existsByTitle(String title) {
        return articleRepository.existsByTitle(title);
    }

    public ArticleSummaryResponseDTO getByTitle(String title) {
        Article article = articleRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with title: " + title));
        return getSummaryDTO(article);
    }

    // get filtered and/or ordered lists

    public Page<ArticleSummaryResponseDTO> getFilteredList(FilterRequestDTO filterRequestDTO){
        if(filterRequestDTO.getOrderBy() == null) filterRequestDTO.setOrderBy("recent");
        Pageable pageable = Pageable.ofSize(filterRequestDTO.getPageSize()).withPage(filterRequestDTO.getPageNum());
        List<String> types = filterRequestDTO.getCategories();
        if(types.isEmpty()){
            return switch (filterRequestDTO.getOrderBy()) {
                case "likes" ->
                        articleRepository.findLikedArticles(pageable).map(this::getSummaryDTO);
                case "comments" ->
                        articleRepository.findCommentedArticles(pageable).map(this::getSummaryDTO);
                default ->
                        articleRepository.findRecentArticles(pageable).map(this::getSummaryDTO);
            };
        }else{
            return switch (filterRequestDTO.getOrderBy()) {
                case "likes" ->
                        articleRepository.findLikedArticleByType(types, pageable).map(this::getSummaryDTO);
                case "comments" ->
                        articleRepository.findCommentedArticleByType(types, pageable).map(this::getSummaryDTO);
                default ->
                        articleRepository.findRecentArticleByType(types, pageable).map(this::getSummaryDTO);
            };
        }
    }

    // get full article

    public ArticleResponseDTO getFullArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        return getFullDTO(article);
    }

    public ArticleResponseDTO getFullArticleByTitle(String title) {
        Article article = articleRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with title: " + title));
        return getFullDTO(article);
    }

    // create article

    public ArticleResponseDTO createArticle(ArticleRequestDTO article) {
        if (existsByTitle(article.getTitle())) {
            throw new BadRequestException("Article with title: " + article.getTitle() + " already exists");
        }

        ArticleDetail newArticleDetail = articleMapper.convertToDetailEntity(article);
        articleDetailRepository.save(newArticleDetail);

        List<Writer> writers = writerService.getEntities(article.getWriterIds());
        Article newArticle = articleMapper.convertToEntity(article, writers);

        newArticle.setLastUpdateDate(LocalDate.now());
        newArticle.setPublicationDate(LocalDate.now());
        newArticle.setArticleDetail(newArticleDetail);
        articleRepository.save(newArticle);

        return getFullDTO(newArticle);
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
    public void deleteLike(Long articleId, Long userId) {
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

        return getFullDTO(article);
    }
}
