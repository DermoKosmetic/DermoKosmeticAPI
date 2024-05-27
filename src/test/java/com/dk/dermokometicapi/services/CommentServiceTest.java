package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.Article;
import com.dk.dermokometicapi.models.entities.Comment;
import com.dk.dermokometicapi.models.entities.CommentLike;
import com.dk.dermokometicapi.models.entities.User;
import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.mappers.CommentLikeMapper;
import com.dk.dermokometicapi.mappers.CommentMapper;
import com.dk.dermokometicapi.repositories.ArticleRepository;
import com.dk.dermokometicapi.repositories.CommentLikeRepository;
import com.dk.dermokometicapi.repositories.CommentRepository;
import com.dk.dermokometicapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentLikeMapper commentLikeMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testConvertToDTO() {
        // Arrange
        Long id = 1L;
        long likes = 5L;
        long commentParent = 2L;

        Comment comment = new Comment();
        comment.setId(id);
        comment.setPublicationDate(LocalDate.now());

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(id);
        commentResponseDTO.setParentCommentId(commentParent);
        commentResponseDTO.setLikeNumber(likes);

        when(commentLikeRepository.countByComment(comment)).thenReturn(likes);
        when(commentRepository.countByParentComment(comment)).thenReturn(commentParent);
        when(commentMapper.convertToDTO(comment, likes, commentParent)).thenReturn(commentResponseDTO);

        // Act
        CommentResponseDTO result = commentService.convertToDTO(comment);

        // Assert
        assertNotNull(result);
        assertEquals(commentResponseDTO, result);

        //verify
        verify(commentLikeRepository).countByComment(comment);
        verify(commentRepository).countByParentComment(comment);
        verify(commentMapper).convertToDTO(comment, likes, commentParent);
    }

    @Test
    public void testGetAllComments() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);

        when(commentRepository.findAll()).thenReturn(commentList);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        List<CommentResponseDTO> result = commentService.getAllComments();

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.size());
        assertEquals(commentListDTO, result);

        //verify
        verify(commentRepository).findAll();
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testGetCommentById() {
        // Arrange
        Long id = 1L;
        long likes = 5L;
        long commentParent = 2L;

        Comment comment = new Comment();
        comment.setId(id);
        comment.setPublicationDate(LocalDate.now());

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(id);
        commentResponseDTO.setParentCommentId(commentParent);
        commentResponseDTO.setLikeNumber(likes);

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(commentLikeRepository.countByComment(comment)).thenReturn(likes);
        when(commentRepository.countByParentComment(comment)).thenReturn(commentParent);
        when(commentMapper.convertToDTO(comment, likes, commentParent)).thenReturn(commentResponseDTO);

        // Act
        CommentResponseDTO result = commentService.getCommentById(id);

        // Assert
        assertNotNull(result);
        assertEquals(commentResponseDTO, result);
        verify(commentLikeRepository).countByComment(comment);
        verify(commentRepository).countByParentComment(comment);
        verify(commentMapper).convertToDTO(comment, likes, commentParent);
    }

    @Test
    public void testGetCommentById_NotFound() {
        // Arrange
        Long id = 1L;

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(id));
        verify(commentRepository).findById(id);
    }

    @Test
    public void testAddComment() {
        // Arrange
        Long userId = 1L;
        Long articleId = 2L;

        User user = new User();
        user.setId(userId);
        Article article = new Article();
        article.setId(articleId);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPublicationDate(LocalDate.now());
        comment.setArticle(article);
        comment.setUser(user);
        comment.setParentComment(null);

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(1L);
        commentResponseDTO.setPublicationDate(LocalDate.now().toString());
        commentResponseDTO.setArticleId(articleId);
        commentResponseDTO.setUserId(userId);

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setUserId(userId);
        commentRequestDTO.setArticleId(articleId);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(commentMapper.convertToEntity(commentRequestDTO, article, null, user)).thenReturn(comment);
        when(commentMapper.convertToDTO(comment, 0L, 0L)).thenReturn(commentResponseDTO);

        // Act
        CommentResponseDTO result = commentService.addComment(commentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentResponseDTO, result);
    }

    @Test
    public void testAddComment_ParentCommentNotFound() {
        // Arrange
        Long userId = 1L;
        Long articleId = 2L;
        Long parentCommentId = 3L;

        User user = new User();
        user.setId(userId);
        Article article = new Article();
        article.setId(articleId);
        Comment parentComment = new Comment();
        parentComment.setId(parentCommentId);

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(1L);
        commentResponseDTO.setPublicationDate(LocalDate.now().toString());
        commentResponseDTO.setArticleId(articleId);
        commentResponseDTO.setUserId(userId);
        commentResponseDTO.setParentCommentId(parentCommentId);

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setUserId(userId);
        commentRequestDTO.setArticleId(articleId);
        commentRequestDTO.setParentCommentId(parentCommentId);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentRequestDTO));

        //verify
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(commentRepository).findById(parentCommentId);
    }

    @Test
    public void testAddComment_ArticleNotFound() {
        // Arrange
        Long userId = 1L;
        Long articleId = 2L;
        Long parentCommentId = 3L;

        User user = new User();
        user.setId(userId);
        Article article = new Article();
        article.setId(articleId);
        Comment parentComment = new Comment();
        parentComment.setId(parentCommentId);

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setUserId(userId);
        commentRequestDTO.setArticleId(articleId);
        commentRequestDTO.setParentCommentId(parentCommentId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentRequestDTO));

        //verify
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
    }

    @Test
    public void testAddComment_EmailNotFound() {
        // Arrange
        Long userId = 1L;
        Long articleId = 2L;
        Long parentCommentId = 3L;

        User user = new User();
        user.setId(userId);

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setUserId(userId);
        commentRequestDTO.setArticleId(articleId);
        commentRequestDTO.setParentCommentId(parentCommentId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentRequestDTO));

        //verify
        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetCommentsByArticleId_Likes() {
        // Arrange
        Long articleId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("likes");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Pageable pageable = PageRequest.of(listRequestDTO.getPageNum(), listRequestDTO.getPageSize());
        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, commentList.size());

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);
        Page<CommentResponseDTO> commentResponseDTOPage = new PageImpl<>(commentListDTO, pageable, commentListDTO.size());

        when(commentRepository.findLikedCommentsByArticle_id(articleId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()))).thenReturn(commentPage);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByArticleId(articleId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.getSize());
        assertEquals(commentResponseDTOPage, result);

        //verify
        verify(commentRepository).findLikedCommentsByArticle_id(articleId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()));
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testGetCommentsByArticleId_Comments() {
        // Arrange
        Long articleId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("comments");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Pageable pageable = PageRequest.of(listRequestDTO.getPageNum(), listRequestDTO.getPageSize());
        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, commentList.size());

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);
        Page<CommentResponseDTO> commentResponseDTOPage = new PageImpl<>(commentListDTO, pageable, commentListDTO.size());

        when(commentRepository.findCommentedCommentsByArticle_id(articleId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()))).thenReturn(commentPage);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByArticleId(articleId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.getSize());
        assertEquals(commentResponseDTOPage, result);

        //verify
        verify(commentRepository).findCommentedCommentsByArticle_id(articleId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()));
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testGetCommentsByArticleId_Default() {
        // Arrange
        Long articleId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("default");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Pageable pageable = PageRequest.of(listRequestDTO.getPageNum(), listRequestDTO.getPageSize());
        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, commentList.size());

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);
        Page<CommentResponseDTO> commentResponseDTOPage = new PageImpl<>(commentListDTO, pageable, commentListDTO.size());

        when(commentRepository.findRecentCommentsByArticle_id(articleId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()))).thenReturn(commentPage);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByArticleId(articleId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.getSize());
        assertEquals(commentResponseDTOPage, result);

        //verify
        verify(commentRepository).findRecentCommentsByArticle_id(articleId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()));
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testGetCommentsByParentId_Likes() {
        // Arrange
        Long parentId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("likes");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Pageable pageable = PageRequest.of(listRequestDTO.getPageNum(), listRequestDTO.getPageSize());
        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, commentList.size());

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);
        Page<CommentResponseDTO> commentResponseDTOPage = new PageImpl<>(commentListDTO, pageable, commentListDTO.size());

        when(commentRepository.findLikedCommentsByParentComment_id(parentId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()))).thenReturn(commentPage);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByParentId(parentId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.getSize());
        assertEquals(commentResponseDTOPage, result);

        //verify
        verify(commentRepository).findLikedCommentsByParentComment_id(parentId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()));
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testGetCommentsByParentId_Comments() {
        // Arrange
        Long parentId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("comments");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Pageable pageable = PageRequest.of(listRequestDTO.getPageNum(), listRequestDTO.getPageSize());
        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, commentList.size());

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);
        Page<CommentResponseDTO> commentResponseDTOPage = new PageImpl<>(commentListDTO, pageable, commentListDTO.size());

        when(commentRepository.findCommentedCommentsByParentComment_id(parentId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()))).thenReturn(commentPage);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByParentId(parentId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.getSize());
        assertEquals(commentResponseDTOPage, result);

        //verify
        verify(commentRepository).findCommentedCommentsByParentComment_id(parentId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()));
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testGetCommentByParentId_Default() {
        // Arrange
        Long parentId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("default");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Pageable pageable = PageRequest.of(listRequestDTO.getPageNum(), listRequestDTO.getPageSize());
        Page<Comment> commentPage = new PageImpl<>(List.of(comment1, comment2), pageable, commentList.size());

        CommentResponseDTO commentResponseDTO1 = new CommentResponseDTO();
        commentResponseDTO1.setId(1L);
        CommentResponseDTO commentResponseDTO2 = new CommentResponseDTO();
        commentResponseDTO2.setId(2L);
        List<CommentResponseDTO> commentListDTO = Arrays.asList(commentResponseDTO1, commentResponseDTO2);
        Page<CommentResponseDTO> commentResponseDTOPage = new PageImpl<>(commentListDTO, pageable, commentListDTO.size());

        when(commentRepository.findRecentCommentsByParentComment_id(parentId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()))).thenReturn(commentPage);
        when(commentMapper.convertToDTO(comment1, 0L, 0L)).thenReturn(commentResponseDTO1);
        when(commentMapper.convertToDTO(comment2, 0L, 0L)).thenReturn(commentResponseDTO2);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByParentId(parentId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentListDTO.size(), result.getSize());
        assertEquals(commentResponseDTOPage, result);

        //verify
        verify(commentRepository).findRecentCommentsByParentComment_id(parentId, Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum()));
        verify(commentMapper).convertToDTO(comment1, 0L, 0L);
        verify(commentMapper).convertToDTO(comment2, 0L, 0L);
    }

    @Test
    public void testDeleteComment(){
        // Arrange
        Long id = 1L;
        Comment comment = new Comment();
        comment.setId(id);

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        // Act
        assertDoesNotThrow(()->commentService.deleteComment(id));

        //verify
        verify(commentRepository).findById(id);
        verify(commentRepository).deleteById(id);
    }

    @Test
    public void testDeleteComment_NotFound(){
        // Arrange
        Long id = 1L;

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(id));

        //verify
        verify(commentRepository).findById(id);
    }

    @Test
    public void testAddLike() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(comment.getId());
        commentLikeRequestDTO.setUserId(user.getId());

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setPublicationDate(LocalDate.now());

        CommentLikeResponseDTO commentLikeResponseDTO = new CommentLikeResponseDTO();
        commentLikeResponseDTO.setCommentId(comment.getId());
        commentLikeResponseDTO.setUserId(user.getId());

        when(commentRepository.findById(userId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentLikeRepository.existsByCommentAndUser(comment, user)).thenReturn(false);
        when(commentLikeRepository.save(commentLike)).thenReturn(commentLike);
        when(commentLikeMapper.convertToDTO(commentLike)).thenReturn(commentLikeResponseDTO);

        // Act
        CommentLikeResponseDTO result = commentService.addLike(commentLikeRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(commentLikeResponseDTO, result);
        //verify
        verify(commentRepository).findById(userId);
        verify(userRepository).findById(userId);
        verify(commentLikeRepository).existsByCommentAndUser(comment, user);
        verify(commentLikeRepository).save(commentLike);
    }

    @Test
    public void testAddLike_CommentAndUserAlreadyExists() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(comment.getId());
        commentLikeRequestDTO.setUserId(user.getId());


        CommentLikeResponseDTO commentLikeResponseDTO = new CommentLikeResponseDTO();
        commentLikeResponseDTO.setCommentId(comment.getId());
        commentLikeResponseDTO.setUserId(user.getId());

        when(commentRepository.findById(userId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentLikeRepository.existsByCommentAndUser(comment, user)).thenReturn(true);

        // Act
        assertThrows(BadRequestException.class,()-> commentService.addLike(commentLikeRequestDTO));

        //verify
        verify(commentRepository).findById(userId);
        verify(userRepository).findById(userId);
        verify(commentLikeRepository).existsByCommentAndUser(comment, user);
    }

    @Test
    public void testAddLike_UserNotFound() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(comment.getId());
        commentLikeRequestDTO.setUserId(user.getId());

        when(commentRepository.findById(userId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class,()-> commentService.addLike(commentLikeRequestDTO));

        //verify
        verify(commentRepository).findById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    public void testAddLike_IdNotFound() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(comment.getId());
        commentLikeRequestDTO.setUserId(user.getId());

        when(commentRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class,()-> commentService.addLike(commentLikeRequestDTO));

        //verify
        verify(commentRepository).findById(userId);
    }

    @Test
    public void testDeleteLike(){
        // Arrange
        Long id = 1L;
        CommentLike commentlike = new CommentLike();
        commentlike.setId(id);

        when(commentLikeRepository.findById(id)).thenReturn(Optional.of(commentlike));
        // Act
        assertDoesNotThrow(()->commentService.deleteLike(id));

        //verify
        verify(commentLikeRepository).findById(id);
        verify(commentLikeRepository).deleteById(id);
    }

    @Test
    public void testDeleteLike_NotFound(){
        // Arrange
        Long id = 1L;
        CommentLike commentlike = new CommentLike();
        commentlike.setId(id);

        when(commentLikeRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        assertThrows(ResourceNotFoundException.class,()->commentService.deleteLike(id));

        //verify
        verify(commentLikeRepository).findById(id);
    }

    @Test
    public void testDeleteLikeArguments(){
        // Arrange
        Long id = 1L;

        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(1L);

        CommentLike commentlike = new CommentLike();
        commentlike.setId(id);
        commentlike.setUser(user);
        commentlike.setComment(comment);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(commentlike.getComment().getId());
        commentLikeRequestDTO.setUserId(commentlike.getUser().getId());

        when(commentLikeRepository.findByComment_IdAndUser_Id(commentLikeRequestDTO.getCommentId(),commentLikeRequestDTO.getUserId())).thenReturn(Optional.of(commentlike));

        // Act
        assertDoesNotThrow(()->commentService.deleteLike(commentLikeRequestDTO));

        //verify
        verify(commentLikeRepository).findByComment_IdAndUser_Id(commentLikeRequestDTO.getCommentId(),commentLikeRequestDTO.getUserId());
        verify(commentLikeRepository).delete(commentlike);
    }

    @Test
    public void testDeleteLikeArguments_CommentLikeNotFound(){
        // Arrange
        Long id = 1L;

        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(1L);

        CommentLike commentlike = new CommentLike();
        commentlike.setId(id);
        commentlike.setUser(user);
        commentlike.setComment(comment);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(commentlike.getComment().getId());
        commentLikeRequestDTO.setUserId(commentlike.getUser().getId());

        when(commentLikeRepository.findByComment_IdAndUser_Id(commentLikeRequestDTO.getCommentId(),commentLikeRequestDTO.getUserId())).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class,()->commentService.deleteLike(commentLikeRequestDTO));

        //verify
        verify(commentLikeRepository).findByComment_IdAndUser_Id(commentLikeRequestDTO.getCommentId(),commentLikeRequestDTO.getUserId());
    }


}
