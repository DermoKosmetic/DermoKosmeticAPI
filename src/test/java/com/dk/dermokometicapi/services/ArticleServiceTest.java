package com.dk.dermokometicapi.services;


import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.entity.*;
import com.dk.dermokometicapi.model.exception.BadRequestException;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.ArticleLikeMapper;
import com.dk.dermokometicapi.model.mapper.ArticleMapper;
import com.dk.dermokometicapi.model.repository.ArticleDetailRepository;
import com.dk.dermokometicapi.model.repository.ArticleLikeRepository;
import com.dk.dermokometicapi.model.repository.ArticleRepository;
import com.dk.dermokometicapi.model.service.ArticleService;
import com.dk.dermokometicapi.model.service.UserService;
import com.dk.dermokometicapi.model.service.WriterService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private WriterService writerService;

    @Mock
    private ArticleLikeRepository articleLikeRepository;

    @Mock
    private UserService userService;

    @Mock
    private ArticleLikeMapper articleLikeMapper;

    @Mock
    private ArticleDetailRepository articleDetailRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    public void testGetAllArticles(){
        // Arrange
        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);

        List<Article> articles = Arrays.asList(article1, article2);

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO();
        dto1.setId(article1.getId());
        dto1.setTitle(article1.getTitle());
        dto1.setDescription(article1.getDescription());
        dto1.setType(article1.getType());
        dto1.setMainImg(article1.getMainImg());
        dto1.setPublicationDate(LocalDate.now().toString());
        dto1.setLastUpdateDate(LocalDate.now().toString());
        dto1.setLikes(likes1);
        dto1.setComments(comments1);

        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO();
        dto2.setId(article2.getId());
        dto2.setTitle(article2.getTitle());
        dto2.setDescription(article2.getDescription());
        dto2.setType(article2.getType());
        dto2.setMainImg(article2.getMainImg());
        dto2.setPublicationDate(LocalDate.now().toString());
        dto2.setLastUpdateDate(LocalDate.now().toString());
        dto2.setLikes(likes2);
        dto2.setComments(comments2);

        when(articleRepository.getAll()).thenReturn(articles);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);
        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        List<ArticleSummaryResponseDTO> result = articleService.getAllArticles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(articleRepository, times(1)).getAll();
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testDeleteById(){
        // Arrange
        Long articleDetailId = 1L;
        Long articleId = 1L;

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setId(articleDetailId);

        Article article = new Article();
        article.setId(articleId);
        article.setArticleDetail(articleDetail);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // Act
        articleService.deleteById(articleId);

        // Assert
        verify(articleRepository, times(1)).findById(articleId);
        verify(articleRepository, times(1)).deleteById(articleId);
        verify(articleDetailRepository, times(1)).delete(articleDetail);
    }

    @Test
    public void testDeleteById_ArticleNotFound() {
        // Arrange
        Long articleId = 1L;

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.deleteById(articleId));

    }

    @Test
    public void testDeleteByTitle(){
        // Arrange
        Long articleDetailId = 1L;
        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setId(articleDetailId);

        Long articleId = 1L;
        String title = "title";
        Article article = new Article();
        article.setId(articleId);
        article.setTitle(title);

        article.setArticleDetail(articleDetail);

        when (articleRepository.findByTitle(title)).thenReturn(Optional.of(article));

        // Act
        articleService.deleteByTitle(title);

        // Assert
        verify(articleRepository, times(1)).findByTitle(title);
        verify(articleRepository, times(1)).deleteByTitle(title);
        verify(articleDetailRepository, times(1)).delete(articleDetail);
    }

    @Test
    public void testDeleteByTitle_ArticleNotFound() {
        // Arrange
        String title = "Title1";

        when(articleRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.deleteByTitle(title));
    }

    @Test
    public void testExistsByTitle() {
        // Arrange
        String title = "Title1";
        when(articleRepository.existsByTitle(title)).thenReturn(true);

        // Act
        boolean result = articleService.existsByTitle(title);

        // Assert
        assertTrue(result);
        verify(articleRepository, times(1)).existsByTitle(title);
    }

    @Test
    public void testExistsByTitle_ArticleNotExists() {
        // Arrange
        String title = "Title1";
        when(articleRepository.existsByTitle(title)).thenReturn(false);

        // Act
        boolean result = articleService.existsByTitle(title);

        // Assert
        assertFalse(result);
        verify(articleRepository, times(1)).existsByTitle(title);
    }

    //DONE
    @Test
    public void testGetByTitle() {
        // Arrange
        String title = "Title1";
        Article article = new Article();
        article.setTitle(title);

        Long likes = 10L;
        Long comments = 5L;

        ArticleSummaryResponseDTO articleSummaryResponseDTO = new ArticleSummaryResponseDTO();
        articleSummaryResponseDTO.setTitle(title);
        articleSummaryResponseDTO.setLikes(likes);
        articleSummaryResponseDTO.setComments(comments);

        when(articleRepository.findByTitle(title)).thenReturn(Optional.of(article));
        when(articleRepository.findArticleLikesById(article.getId())).thenReturn(likes);
        when(articleRepository.findArticleCommentsById(article.getId())).thenReturn(comments);

        when(articleMapper.convertToSummaryDTO(article, likes, comments)).thenReturn(articleSummaryResponseDTO);

        // Act
        ArticleSummaryResponseDTO result = articleService.getByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(articleSummaryResponseDTO, result);
        verify(articleRepository, times(1)).findByTitle(title);
        verify(articleRepository, times(1)).findArticleLikesById(article.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article, likes, comments);
    }

    @Test
    public void testGetByTitle_ArticleNotFound() {
        // Arrange
        String title = "Title";

        when(articleRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.getByTitle(title));

        verify(articleRepository, times(1)).findByTitle(title);

        verify(articleRepository, never()).findArticleLikesById(anyLong());
        verify(articleRepository, never()).findArticleCommentsById(anyLong());
        verify(articleMapper, never()).convertToSummaryDTO(any(Article.class), anyLong(), anyLong());
    }

    @Test
    public void testGetAllArticlesSummary() {
        // Arrange

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail1);
        article2.setArticleDetail(articleDetail2);

        List<Article> articles = Arrays.asList(article1, article2);

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO();
        dto1.setId(article1.getId());
        dto1.setTitle(article1.getTitle());
        dto1.setDescription(article1.getDescription());
        dto1.setType(article1.getType());
        dto1.setMainImg(article1.getMainImg());
        dto1.setPublicationDate(LocalDate.now().toString());
        dto1.setLastUpdateDate(LocalDate.now().toString());
        dto1.setLikes(likes1);
        dto1.setComments(comments1);

        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO();
        dto2.setId(article2.getId());
        dto2.setTitle(article2.getTitle());
        dto2.setDescription(article2.getDescription());
        dto2.setType(article2.getType());
        dto2.setMainImg(article2.getMainImg());
        dto2.setPublicationDate(LocalDate.now().toString());
        dto2.setLastUpdateDate(LocalDate.now().toString());
        dto2.setLikes(likes2);
        dto2.setComments(comments2);

        when(articleRepository.findAll()).thenReturn(articles);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);

        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        List<ArticleSummaryResponseDTO> result = articleService.getAllArticlesSummary();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(articleRepository, times(1)).findAll();
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetRecentArticles() {
        // Arrange
        int pageNum = 0;
        int pageSize = 2;

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);

        List<Article> articleList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articleList, PageRequest.of(pageNum, pageSize), articleList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO();
        dto1.setId(article1.getId());
        dto1.setTitle(article1.getTitle());
        dto1.setDescription(article1.getDescription());
        dto1.setType(article1.getType());
        dto1.setMainImg(article1.getMainImg());
        dto1.setPublicationDate(article1.getPublicationDate().toString());
        dto1.setLastUpdateDate(article1.getLastUpdateDate().toString());
        dto1.setLikes(likes1);
        dto1.setComments(comments1);

        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO();
        dto2.setId(article2.getId());
        dto2.setTitle(article2.getTitle());
        dto2.setDescription(article2.getDescription());
        dto2.setType(article2.getType());
        dto2.setMainImg(article2.getMainImg());
        dto2.setPublicationDate(article2.getPublicationDate().toString());
        dto2.setLastUpdateDate(article2.getLastUpdateDate().toString());
        dto2.setLikes(likes2);
        dto2.setComments(comments2);

        when(articleRepository.findRecentArticles(PageRequest.of(pageNum, pageSize))).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);
        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getRecentArticles(pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getSize());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findRecentArticles(PageRequest.of(pageNum, pageSize));
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetArticlesOrderedByLikes() {
        // Arrange
        int pageNum = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);
        List<Article> articlesList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articlesList, pageRequest, articlesList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO(1L, "Title1", "Description1", "Type1", "image1.png", LocalDate.now().toString(), LocalDate.now().toString(), likes1, comments1);
        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO(2L, "Title2", "Description2", "Type2", "image2.png", LocalDate.now().toString(), LocalDate.now().toString(), likes2, comments2);

        when(articleRepository.findLikedArticles(pageRequest)).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getArticlesOrderedByLikes(pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findLikedArticles(pageRequest);
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetArticlesOrderedByComments() {
        // Arrange
        int pageNum = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);

        List<Article> articlesList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articlesList, pageRequest, articlesList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO(1L, "Title1", "Description1", "Type1", "image1.png", LocalDate.now().toString(), LocalDate.now().toString(), likes1, comments1);
        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO(2L, "Title2", "Description2", "Type2", "image2.png", LocalDate.now().toString(), LocalDate.now().toString(), likes2, comments2);

        when(articleRepository.findCommentedArticles(pageRequest)).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getArticlesOrderedByComments(pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findCommentedArticles(pageRequest);
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetArticlesByType() {
        // Arrange
        List<String> types = Arrays.asList("Type1", "Type2");
        int pageNum = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);

        List<Article> articlesList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articlesList, pageRequest, articlesList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO(1L, "Title1", "Description1", "Type1", "image1.png", LocalDate.now().toString(), LocalDate.now().toString(), likes1, comments1);
        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO(2L, "Title2", "Description2", "Type2", "image2.png", LocalDate.now().toString(), LocalDate.now().toString(), likes2, comments2);

        when(articleRepository.findByType(types, pageRequest)).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getArticlesByType(types, pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findByType(types, pageRequest);
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetLikedArticlesByType() {
        // Arrange
        List<String> types = Arrays.asList("Type1", "Type2");
        int pageNum = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);

        List<Article> articlesList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articlesList, pageRequest, articlesList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO(1L, "Title1", "Description1", "Type1", "image1.png", LocalDate.now().toString(), LocalDate.now().toString(), likes1, comments1);
        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO(2L, "Title2", "Description2", "Type2", "image2.png", LocalDate.now().toString(), LocalDate.now().toString(), likes2, comments2);

        when(articleRepository.findLikedArticleByType(types, pageRequest)).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getLikedArticlesByType(types, pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findLikedArticleByType(types, pageRequest);
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetCommentedArticlesByType() {
        // Arrange
        List<String> types = Arrays.asList("Type1", "Type2");
        int pageNum = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);
        List<Article> articlesList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articlesList, pageRequest, articlesList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO(1L, "Title1", "Description1", "Type1", "image1.png", LocalDate.now().toString(), LocalDate.now().toString(), likes1, comments1);
        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO(2L, "Title2", "Description2", "Type2", "image2.png", LocalDate.now().toString(), LocalDate.now().toString(), likes2, comments2);

        when(articleRepository.findCommentedArticleByType(types, pageRequest)).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getCommentedArticlesByType(types, pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findCommentedArticleByType(types, pageRequest);
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }


    @Test
    public void testGetFilteredList() {
        // Arrange
        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setOrderBy("likes");
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setPageSize(2);
        filterRequestDTO.setCategories(Arrays.asList("Type1", "Type2"));

        PageRequest pageRequest = PageRequest.of(filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());

        ArticleDetail articleDetail1 = new ArticleDetail();
        articleDetail1.setId(1L);
        articleDetail1.setContent("Content1");

        ArticleDetail articleDetail2 = new ArticleDetail();
        articleDetail2.setId(2L);
        articleDetail2.setContent("Content2");

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Title1");
        article1.setDescription("Description1");
        article1.setType("Type1");
        article1.setMainImg("image1.png");
        article1.setPublicationDate(LocalDate.now());
        article1.setLastUpdateDate(LocalDate.now());
        article1.setArticleDetail(articleDetail1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Title2");
        article2.setDescription("Description2");
        article2.setType("Type2");
        article2.setMainImg("image2.png");
        article2.setPublicationDate(LocalDate.now());
        article2.setLastUpdateDate(LocalDate.now());
        article2.setArticleDetail(articleDetail2);
        List<Article> articlesList = Arrays.asList(article1, article2);
        Page<Article> articlesPage = new PageImpl<>(articlesList, pageRequest, articlesList.size());

        Long likes1 = 10L;
        Long comments1 = 5L;
        Long likes2 = 20L;
        Long comments2 = 15L;

        ArticleSummaryResponseDTO dto1 = new ArticleSummaryResponseDTO(1L, "Title1", "Description1", "Type1", "image1.png", LocalDate.now().toString(), LocalDate.now().toString(), likes1, comments1);
        ArticleSummaryResponseDTO dto2 = new ArticleSummaryResponseDTO(2L, "Title2", "Description2", "Type2", "image2.png", LocalDate.now().toString(), LocalDate.now().toString(), likes2, comments2);

        when(articleRepository.findLikedArticleByType(filterRequestDTO.getCategories(), pageRequest)).thenReturn(articlesPage);
        when(articleRepository.findArticleLikesById(article1.getId())).thenReturn(likes1);
        when(articleRepository.findArticleCommentsById(article1.getId())).thenReturn(comments1);
        when(articleRepository.findArticleLikesById(article2.getId())).thenReturn(likes2);
        when(articleRepository.findArticleCommentsById(article2.getId())).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(articleRepository, times(1)).findLikedArticleByType(filterRequestDTO.getCategories(), pageRequest);
        verify(articleRepository, times(1)).findArticleLikesById(article1.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article1.getId());
        verify(articleRepository, times(1)).findArticleLikesById(article2.getId());
        verify(articleRepository, times(1)).findArticleCommentsById(article2.getId());
        verify(articleMapper, times(1)).convertToSummaryDTO(article1, likes1, comments1);
        verify(articleMapper, times(1)).convertToSummaryDTO(article2, likes2, comments2);
    }

    @Test
    public void testGetFullArticleById() {
        // Arrange
        Long id = 1L;
        Article article = new Article();
        article.setId(id);
        when(articleRepository.findById(id)).thenReturn(Optional.of(article));

        long likes = 1; long comments = 2;
        when(articleRepository.findArticleLikesById(id)).thenReturn(likes);
        when(articleRepository.findArticleCommentsById(id)).thenReturn(comments);

        List<Long> writers = Arrays.asList(1L, 2L);
        when(articleRepository.findWritersByArticleId(id)).thenReturn(writers);

        ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
        articleResponseDTO.setId(article.getId());
        articleResponseDTO.setLikes(likes);
        articleResponseDTO.setComments(comments);
        articleResponseDTO.setWriterIds(writers);
        when(articleMapper.convertToDTO(article,article.getArticleDetail(), likes, comments, writers)).thenReturn(articleResponseDTO);

        // Act
        ArticleResponseDTO result = articleService.getFullArticleById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());

        //Verify
        verify(articleRepository, times(1)).findById(id);
        verify(articleRepository, times(1)).findArticleLikesById(id);
        verify(articleRepository, times(1)).findArticleCommentsById(id);
        verify(articleRepository, times(1)).findWritersByArticleId(id);
        verify(articleMapper, times(1)).convertToDTO(article, article.getArticleDetail(), likes, comments, writers);
    }

    @Test
    public void testGetFullArticleById_NotFound() {
        // Arrange
        Long id = 1L;
        when(articleRepository.findById(id)).thenReturn(Optional.empty());

        // Assert and Act
        String expectedMessage = "Article not found with id: " + id;
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->articleService.getFullArticleById(id));
        assertEquals(expectedMessage, exception.getMessage());

        //Verify
        verify(articleRepository, times(1)).findById(id);
    }

    @Test
    public void testCreateArticle() {
        // Arrange

        List<Long> writersIds = Arrays.asList(1L, 2L, 3L);
        Writer writer1 = new Writer(); writer1.setId(1L);
        Writer writer2 = new Writer(); writer2.setId(2L);
        Writer writer3 = new Writer(); writer3.setId(3L);
        List<Writer> writers = Arrays.asList(writer1, writer2, writer3);

        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        articleRequestDTO.setTitle("title");
        articleRequestDTO.setContent("content");
        articleRequestDTO.setWriterIds(writersIds);
        when(articleRepository.existsByTitle(articleRequestDTO.getTitle())).thenReturn(false);
        when(writerService.getEntities(articleRequestDTO.getWriterIds())).thenReturn(writers);

        Article article = new Article();
        article.setId(1L);
        article.setWriters(writers);
        article.setTitle(articleRequestDTO.getTitle());
        when(articleMapper.convertToEntity(articleRequestDTO, writers)).thenReturn(article);

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setId(1L);
        articleDetail.setContent(articleRequestDTO.getContent());
        article.setArticleDetail(articleDetail);
        when(articleMapper.convertToDetailEntity(articleRequestDTO, article)).thenReturn(articleDetail);

        ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
        articleResponseDTO.setId(article.getId());
        articleResponseDTO.setWriterIds(articleRequestDTO.getWriterIds());
        articleResponseDTO.setTitle(article.getTitle());
        articleResponseDTO.setContent(articleRequestDTO.getContent());
        when(articleMapper.convertToDTO(article, article.getArticleDetail(), 0L, 0L, articleRequestDTO.getWriterIds())).thenReturn(articleResponseDTO);

        // Act
        ArticleResponseDTO result = articleService.createArticle(articleRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(article.getId(), result.getId());
        assertEquals(articleRequestDTO.getTitle(), result.getTitle());
        assertEquals(articleRequestDTO.getContent(), result.getContent());

        //Verify
        verify(writerService, times(1)).getEntities(articleRequestDTO.getWriterIds());
        verify(articleMapper, times(1)).convertToEntity(articleRequestDTO, writers);
        verify(articleMapper, times(1)).convertToDetailEntity(articleRequestDTO, article);
        verify(articleRepository, times(1)).save(article);
        verify(articleDetailRepository, times(1)).save(article.getArticleDetail());
        verify(articleMapper, times(1)).convertToDTO(article, article.getArticleDetail(), 0L, 0L, articleRequestDTO.getWriterIds());

    }

    @Test
    public void testCreateArticle_ArticleAlreadyExists() {
        // Arrange
        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        articleRequestDTO.setTitle("title");
        when(articleRepository.existsByTitle(articleRequestDTO.getTitle())).thenReturn(true);

        // Assert and Act
        String expectedMessage = "Article with title: " + articleRequestDTO.getTitle() + " already exists";
        BadRequestException exception = assertThrows(BadRequestException.class, ()->articleService.createArticle(articleRequestDTO));
        assertEquals(expectedMessage, exception.getMessage());

        //Verify
        verify(articleRepository, times(1)).existsByTitle(articleRequestDTO.getTitle());
    }

    @Test
    public void testCreateLike() {

        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        ArticleLikeRequestDTO requestDTO = new ArticleLikeRequestDTO();
        requestDTO.setArticleId(1L);
        requestDTO.setUserId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userService.getEntityById(1L)).thenReturn(user);
        when(articleLikeRepository.existsByArticleAndUser(article, user)).thenReturn(false);

        ArticleLike newLike = new ArticleLike();
        newLike.setArticle(article);
        newLike.setUser(user);
        newLike.setLikeDate(LocalDate.now());

        when(articleLikeRepository.save(any(ArticleLike.class))).thenReturn(newLike);
        when(articleLikeMapper.convertToResponseDTO(newLike)).thenReturn(new ArticleLikeResponseDTO());

        // Act
        ArticleLikeResponseDTO responseDTO = articleService.createLike(requestDTO);

        // Assert
        assertNotNull(responseDTO);

        //Verify
        verify(articleRepository).findById(1L);
        verify(userService).getEntityById(1L);
        verify(articleLikeRepository).existsByArticleAndUser(article, user);
        verify(articleLikeRepository).save(any(ArticleLike.class));
        verify(articleLikeMapper).convertToResponseDTO(newLike);
    }

    @Test
    public void testCreateLike_ArticleNotFound() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        ArticleLikeRequestDTO requestDTO = new ArticleLikeRequestDTO();
        requestDTO.setArticleId(1L);
        requestDTO.setUserId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.createLike(requestDTO));

        //Verify
        verify(articleRepository).findById(1L);
    }

    @Test
    public void testCreateLike_UserAlreadyLiked() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        ArticleLikeRequestDTO requestDTO = new ArticleLikeRequestDTO();
        requestDTO.setArticleId(1L);
        requestDTO.setUserId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userService.getEntityById(1L)).thenReturn(user);
        when(articleLikeRepository.existsByArticleAndUser(article, user)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> articleService.createLike(requestDTO));
        //verify
        verify(articleRepository).findById(1L);
        verify(userService).getEntityById(1L);
        verify(articleLikeRepository).existsByArticleAndUser(article, user);
    }

    @Test
    public void testDeleteLike() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        ArticleLikeRequestDTO requestDTO = new ArticleLikeRequestDTO();
        requestDTO.setArticleId(1L);
        requestDTO.setUserId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userService.getEntityById(1L)).thenReturn(user);
        when(articleLikeRepository.existsByArticleAndUser(article, user)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> articleService.deleteLike(requestDTO));

        // verify
        verify(articleRepository).findById(1L);
        verify(userService).getEntityById(1L);
        verify(articleLikeRepository).existsByArticleAndUser(article, user);
        verify(articleLikeRepository).deleteByArticleAndUser(article, user);
    }

    @Test
    public void testDeleteLike_ArticleNotFound() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        ArticleLikeRequestDTO requestDTO = new ArticleLikeRequestDTO();
        requestDTO.setArticleId(1L);
        requestDTO.setUserId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.deleteLike(requestDTO));

        //Verify
        verify(articleRepository).findById(1L);
    }

    @Test
    public void testDeleteLike_UserDidNotLike() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        ArticleLikeRequestDTO requestDTO = new ArticleLikeRequestDTO();
        requestDTO.setArticleId(1L);
        requestDTO.setUserId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userService.getEntityById(1L)).thenReturn(user);
        when(articleLikeRepository.existsByArticleAndUser(article, user)).thenReturn(false);

        // Act and Assert
        assertThrows(BadRequestException.class, () -> articleService.deleteLike(requestDTO));

        //Verify
        verify(articleRepository).findById(1L);
        verify(userService).getEntityById(1L);
        verify(articleLikeRepository).existsByArticleAndUser(article, user);
    }

    @Test
    public void testUpdateArticle(){
        // Arrange
        Long articleId = 1L;
        List<Long> writersIds = Arrays.asList(1L, 2L, 3L);
        Writer writer1 = new Writer(); writer1.setId(1L);
        Writer writer2 = new Writer(); writer2.setId(2L);
        Writer writer3 = new Writer(); writer3.setId(3L);
        List<Writer> writers = Arrays.asList(writer1, writer2, writer3);

        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();

        articleRequestDTO.setTitle("title");
        articleRequestDTO.setDescription("description");
        articleRequestDTO.setType("type");
        articleRequestDTO.setMainImg("image");
        articleRequestDTO.setContent("content");
        articleRequestDTO.setWriterIds(writersIds);

        Article article = new Article();
        article.setId(articleId);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setId(1L);
        article.setArticleDetail(articleDetail);
        when(articleDetailRepository.findById(article.getArticleDetail().getId())).thenReturn(Optional.of(articleDetail));
        when(writerService.getEntities(writersIds)).thenReturn(writers);

        article.setTitle(articleRequestDTO.getTitle());
        article.setDescription(articleRequestDTO.getDescription());
        article.setType(articleRequestDTO.getType());
        article.setMainImg(articleRequestDTO.getMainImg());
        article.setWriters(writers);
        article.setLastUpdateDate(LocalDate.now());

        articleDetail.setContent(articleRequestDTO.getContent());

        ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO(1L, article.getTitle(), article.getDescription(), article.getType(), article.getMainImg(), LocalDate.now().toString(), LocalDate.now().toString(), 0L, 0L, writersIds, articleRequestDTO.getContent());
        when(articleMapper.convertToDTO(article, articleDetail, 0L, 0L, writersIds)).thenReturn(articleResponseDTO);

        // Act
        ArticleResponseDTO result = articleService.updateArticle(articleId, articleRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(articleResponseDTO.getTitle(), result.getTitle());
        assertEquals(articleResponseDTO.getDescription(), result.getDescription());
        assertEquals(articleResponseDTO.getType(), result.getType());
        assertEquals(articleResponseDTO.getMainImg(), result.getMainImg());
        assertEquals(articleResponseDTO.getContent(), result.getContent());

        // Verify
        verify(articleRepository, times(1)).findById(articleId);
        verify(articleDetailRepository, times(1)).findById(article.getArticleDetail().getId());
        verify(writerService, times(1)).getEntities(writersIds);
        verify(articleRepository, times(1)).save(article);
        verify(articleDetailRepository, times(1)).save(articleDetail);
    }

    @Test
    public void testUpdateArticle_ArticleNotFound() {
        // Arrange
        Long articleId = 1L;
        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Assert and Act
        String expectedMessage = "Article not found with id: " + articleId;
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->articleService.updateArticle(articleId, articleRequestDTO));
        assertEquals(expectedMessage, exception.getMessage());

        //Verify
        verify(articleRepository, times(1)).findById(articleId);
    }

    @Test
    public void testUpdateArticle_ArticleDetailNotFound() {
        // Arrange
        Long articleId = 1L;
        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        Article article = new Article();
        article.setId(articleId);
        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setId(1L);
        article.setArticleDetail(articleDetail);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleDetailRepository.findById(article.getArticleDetail().getId())).thenReturn(Optional.empty());

        // Assert and Act
        String expectedMessage = "Article detail not found with id: " + article.getArticleDetail().getId();
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->articleService.updateArticle(articleId, articleRequestDTO));
        assertEquals(expectedMessage, exception.getMessage());

        //Verify
        verify(articleRepository, times(1)).findById(articleId);
        verify(articleDetailRepository, times(1)).findById(article.getArticleDetail().getId());
    }

}
