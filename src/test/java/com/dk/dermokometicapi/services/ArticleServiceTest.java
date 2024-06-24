package com.dk.dermokometicapi.services;


import com.dk.dermokometicapi.mappers.*;
import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.*;
import com.dk.dermokometicapi.repositories.*;
import com.dk.dermokometicapi.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

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


        when(articleLikeRepository.countByArticle(article1)).thenReturn(likes1);
        when(articleLikeRepository.countByArticle(article2)).thenReturn(likes2);
        when(commentRepository.countByArticle(article1)).thenReturn(comments1);
        when(commentRepository.countByArticle(article2)).thenReturn(comments2);

        when(articleMapper.convertToSummaryDTO(article1, likes1, comments1)).thenReturn(dto1);
        when(articleMapper.convertToSummaryDTO(article2, likes2, comments2)).thenReturn(dto2);

        // Act
        List<ArticleSummaryResponseDTO> result = articleService.getAllArticles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
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
        when(articleLikeRepository.countByArticle(article)).thenReturn(likes);
        when(commentRepository.countByArticle(article)).thenReturn(comments);

        when(articleMapper.convertToSummaryDTO(article, likes, comments)).thenReturn(articleSummaryResponseDTO);

        // Act
        ArticleSummaryResponseDTO result = articleService.getByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(articleSummaryResponseDTO, result);
    }

    @Test
    public void testGetByTitle_ArticleNotFound() {
        // Arrange
        String title = "Title";

        when(articleRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.getByTitle(title));
    }

    @Test
    public void testGetFilteredList_likesTyped() {
        // Arrange
        List<String> types = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle("Title" + i);
            article.setType("type 1");
            article.setPublicationDate(LocalDate.now());
            article.setLastUpdateDate(LocalDate.now());
            articles.add(article);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Article> page = new PageImpl<>(articles, pageable, articles.size());

        when(articleRepository.findLikedArticleByType(types, pageable)).thenReturn(page);
        when(articleLikeRepository.countByArticle(any(Article.class))).thenReturn(0L);
        when(commentRepository.countByArticle(any(Article.class))).thenReturn(0L);
        when(articleMapper.convertToSummaryDTO(any(Article.class), eq(0L), eq(0L))).thenAnswer(invocation -> {
            Article question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new ArticleSummaryResponseDTO(question.getId(), question.getTitle(), question.getDescription(), question.getType(), question.getMainImg(), question.getPublicationDate().toString(), question.getLastUpdateDate().toString(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setOrderBy("likes");
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        for (int i = 0; i < 5; i++) {
            ArticleSummaryResponseDTO dto = result.getContent().get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
        }
    }

    @Test
    public void testGetFilteredList_commentsTyped() {
        // Arrange
        List<String> types = List.of("type 1");
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle("Title" + i);
            article.setType("type 1");
            article.setPublicationDate(LocalDate.now());
            article.setLastUpdateDate(LocalDate.now());
            articles.add(article);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Article> page = new PageImpl<>(articles, pageable, articles.size());

        when(articleRepository.findCommentedArticleByType(types, pageable)).thenReturn(page);
        when(articleLikeRepository.countByArticle(any(Article.class))).thenReturn(0L);
        when(commentRepository.countByArticle(any(Article.class))).thenReturn(0L);
        when(articleMapper.convertToSummaryDTO(any(Article.class), eq(0L), eq(0L))).thenAnswer(invocation -> {
            Article question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new ArticleSummaryResponseDTO(question.getId(), question.getTitle(), question.getDescription(), question.getType(), question.getMainImg(), question.getPublicationDate().toString(), question.getLastUpdateDate().toString(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setOrderBy("comments");
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        for (int i = 0; i < 5; i++) {
            ArticleSummaryResponseDTO dto = result.getContent().get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
        }
    }

    @Test
    public void testGetFilteredList_recentTyped() {
        // Arrange
        List<String> types = List.of("type 1");
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle("Title" + i);
            article.setType("type 1");
            article.setPublicationDate(LocalDate.now());
            article.setLastUpdateDate(LocalDate.now());
            articles.add(article);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Article> page = new PageImpl<>(articles, pageable, articles.size());

        when(articleRepository.findRecentArticleByType(types, pageable)).thenReturn(page);
        when(articleLikeRepository.countByArticle(any(Article.class))).thenReturn(0L);
        when(commentRepository.countByArticle(any(Article.class))).thenReturn(0L);
        when(articleMapper.convertToSummaryDTO(any(Article.class), eq(0L), eq(0L))).thenAnswer(invocation -> {
            Article question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new ArticleSummaryResponseDTO(question.getId(), question.getTitle(), question.getDescription(), question.getType(), question.getMainImg(), question.getPublicationDate().toString(), question.getLastUpdateDate().toString(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<ArticleSummaryResponseDTO> result = articleService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        for (int i = 0; i < 5; i++) {
            ArticleSummaryResponseDTO dto = result.getContent().get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
        }
    }

    @Test
    public void testGetFullArticleById() {
        // Arrange
        List<Long> writerIds = Arrays.asList(1L, 2L);
        List<Writer> writers = Arrays.asList(new Writer(), new Writer());
        writers.get(0).setId(1L);
        writers.get(1).setId(2L);
        Long id = 1L;
        Article article = new Article();
        article.setId(id);
        article.setWriters(writers);

        long likes = 1;
        long comments = 2;


        ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
        articleResponseDTO.setId(article.getId());
        articleResponseDTO.setLikes(likes);
        articleResponseDTO.setComments(comments);
        articleResponseDTO.setWriterIds(writerIds);

        when(articleRepository.findById(id)).thenReturn(Optional.of(article));
        when(articleMapper.convertToDTO(article, article.getArticleDetail(), likes, comments, writerIds)).thenReturn(articleResponseDTO);
        when(articleLikeRepository.countByArticle(article)).thenReturn(likes);
        when(commentRepository.countByArticle(article)).thenReturn(comments);
        // Act
        ArticleResponseDTO result = articleService.getFullArticleById(id);

        // Assert
        assertNotNull(result);
        assertEquals(result, articleResponseDTO);
        assertEquals(id, result.getId());
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
    }

    @Test
    public void testCreateArticle() {
        // Arrange
        List<Long> writersIds = List.of(1L);
        Writer writer1 = new Writer(); writer1.setId(1L);
        List<Writer> writers = List.of(writer1);

        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        articleRequestDTO.setTitle("title");
        articleRequestDTO.setContent("content");
        articleRequestDTO.setWriterIds(writersIds);

        Article article = new Article();
        article.setId(1L);
        article.setWriters(writers);
        article.setTitle(articleRequestDTO.getTitle());

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setId(1L);
        articleDetail.setContent(articleRequestDTO.getContent());
        article.setArticleDetail(articleDetail);


        ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
        articleResponseDTO.setId(article.getId());
        articleResponseDTO.setWriterIds(articleRequestDTO.getWriterIds());
        articleResponseDTO.setTitle(article.getTitle());
        articleResponseDTO.setContent(articleRequestDTO.getContent());

        when(articleRepository.existsByTitle(articleRequestDTO.getTitle())).thenReturn(false);
        when(writerService.getEntities(articleRequestDTO.getWriterIds())).thenReturn(writers);
        when(articleMapper.convertToEntity(articleRequestDTO, writers)).thenReturn(article);
        when(articleMapper.convertToDetailEntity(articleRequestDTO)).thenReturn(articleDetail);
        when(articleLikeRepository.countByArticle(article)).thenReturn(0L);
        when(commentRepository.countByArticle(article)).thenReturn(0L);
        when(articleMapper.convertToDTO(article, article.getArticleDetail(), 0L, 0L, articleRequestDTO.getWriterIds())).thenReturn(articleResponseDTO);

        // Act
        ArticleResponseDTO result = articleService.createArticle(articleRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(article.getId(), result.getId());
        assertEquals(articleRequestDTO.getTitle(), result.getTitle());
        assertEquals(articleRequestDTO.getContent(), result.getContent());
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
    }

    @Test
    public void testDeleteLike() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userService.getEntityById(1L)).thenReturn(user);
        when(articleLikeRepository.existsByArticleAndUser(article, user)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> articleService.deleteLike(article.getId(), user.getId()));
    }

    @Test
    public void testDeleteLike_ArticleNotFound() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.deleteLike(article.getId(), user.getId()));
    }

    @Test
    public void testDeleteLike_UserDidNotLike() {
        // Arrange
        Article article = new Article();
        article.setId(1L);

        User user = new User();
        user.setId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userService.getEntityById(1L)).thenReturn(user);
        when(articleLikeRepository.existsByArticleAndUser(article, user)).thenReturn(false);

        // Act and Assert
        assertThrows(BadRequestException.class, () -> articleService.deleteLike(article.getId(), user.getId()));
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
    }

    @Test
    public void testGetFullArticleByTitle(){
        // Arrange
        List<Writer> writers = Arrays.asList(new Writer(), new Writer());
        writers.get(0).setId(1L);
        writers.get(1).setId(2L);
        List<Long> ids = writers.stream().map(Writer::getId).toList();

        String title = "title";
        String content = "content";

        ArticleDetail articleDetail = new ArticleDetail(1L, content);

        Article article = new Article();
        article.setId(1L);
        article.setWriters(writers);
        article.setTitle(title);
        article.setArticleDetail(articleDetail);

        ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
        articleResponseDTO.setId(1L);
        articleResponseDTO.setWriterIds(ids);

        Long likes = 5L;
        Long comments = 10L;

        when(articleRepository.findByTitle(title)).thenReturn(Optional.of(article));
        when(articleLikeRepository.countByArticle(article)).thenReturn(likes);
        when(commentRepository.countByArticle(article)).thenReturn(comments);
        when(articleMapper.convertToDTO(article, articleDetail, likes, comments, ids)).thenReturn(articleResponseDTO);

        // Act
        ArticleResponseDTO result = articleService.getFullArticleByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(result, articleResponseDTO);

    }

    @Test
    public void testGetFullArticleByTitle_articleNotFound(){
        // Arrange
        String title = "title";

        when(articleRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> articleService.getFullArticleByTitle(title));
    }

    @Test
    public void testGetTypes(){
        // Arrange
        List<String> types = Arrays.asList("type1", "type2", "type3");
        when(articleRepository.findDistinctTypes()).thenReturn(types);

        // Act
        List<String> result = articleService.getTypes();

        // Assert
        assertNotNull(result);
        assertEquals(types, result);
    }

}
