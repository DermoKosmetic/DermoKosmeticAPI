package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.models.dto.ArticleLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.ArticleLikeResponseDTO;
import com.dk.dermokometicapi.models.dto.ArticleRequestDTO;
import com.dk.dermokometicapi.models.dto.ArticleResponseDTO;
import com.dk.dermokometicapi.models.entity.*;
import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.mappers.ArticleLikeMapper;
import com.dk.dermokometicapi.mappers.ArticleMapper;
import com.dk.dermokometicapi.repositories.ArticleDetailRepository;
import com.dk.dermokometicapi.repositories.ArticleLikeRepository;
import com.dk.dermokometicapi.repositories.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
