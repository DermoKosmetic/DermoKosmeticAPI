package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private EntityManager entityManager;

    private List<Writer>  getWriters() {
        List<Writer> writers = new ArrayList<>(List.of());
        for(int i = 0; i < 5; i++) {
            Writer writer = new Writer();
            writer.setName("Writer " + i);
            writer.setLastName("Surname " + i);
            writer.setDescription("Description " + i);
            writer.setProfilePic("Pic " + i);
            writers.add(writer);
        }
        return writers;
    }

    private Article createTestArticle() {
        Article article = new Article();
        article.setTitle("Title X");
        article.setDescription("Description X");
        article.setMainImg("Img X");
        article.setType("Type X");
        article.setPublicationDate(LocalDate.now());
        article.setLastUpdateDate(LocalDate.now());
        return article;
    }

    private ArticleDetail createTestArticleDetail() {
        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setContent("Content X");
        return articleDetail;
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("Username X");
        user.setEmail("Email X");
        user.setPassword("Password X");
        user.setProfilePic("Pic X");
        return user;
    }

    private Comment createTestComment() {
        Comment comment = new Comment();
        comment.setContent("Content X");
        comment.setPublicationDate(LocalDate.now());
        return comment;
    }

    private CommentLike createTestCommentLike() {
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(createTestComment());
        commentLike.setUser(createTestUser());
        return commentLike;
    }

    private CommentLikeRequestDTO createTestCommentLikeRequestDTO() {
        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(1L);
        commentLikeRequestDTO.setUserId(1L);
        return commentLikeRequestDTO;
    }

    private CommentRequestDTO createTestCommentRequestDTO() {
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setContent("Content X");
        commentRequestDTO.setArticleId(1L);
        commentRequestDTO.setUserId(1L);
        return commentRequestDTO;
    }

    private ListRequestDTO createTestListRequestDTO() {
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("default");
        return listRequestDTO;
    }

    @Test
    public void testGetAllComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/comments"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetCommentById() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        entityManager.persist(comment);
        entityManager.flush();

        Long id = comment.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddComment() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        CommentRequestDTO commentRequestDTO = createTestCommentRequestDTO();
        commentRequestDTO.setArticleId(article.getId());
        commentRequestDTO.setUserId(user.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testGetCommentsByArticleId() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        entityManager.persist(comment);


        Long articleId = article.getId();
        ListRequestDTO listRequestDTO = createTestListRequestDTO();
        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/article/{id}", articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(listRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetCommentByParentId() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment parentComment = createTestComment();
        parentComment.setArticle(article);
        parentComment.setUser(user);
        entityManager.persist(parentComment);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        comment.setParentComment(parentComment);
        entityManager.persist(comment);
        entityManager.flush();

        Long parentId = parentComment.getId();
        ListRequestDTO listRequestDTO = createTestListRequestDTO();

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/parent/{id}", parentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(listRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteComment() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        entityManager.persist(comment);
        entityManager.flush();

        Long id = comment.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testLikeComment() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        entityManager.persist(comment);

        CommentLikeRequestDTO commentLikeRequestDTO = createTestCommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(comment.getId());
        commentLikeRequestDTO.setUserId(user.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/comments/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentLikeRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDislikeComment() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        entityManager.persist(comment);

        CommentLike commentLike = createTestCommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setPublicationDate(LocalDate.now());
        entityManager.persist(commentLike);
        entityManager.flush();

        Long likeId = commentLike.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/like/{id}", likeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Comment unliked successfully"));
    }

    @Test
    public void testDeleteLikeByCommentIdAndUserId() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        List<Writer> writers = getWriters();
        for(Writer writer : writers) {
            entityManager.persist(writer);
        }

        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        article.setWriters(writers);
        entityManager.persist(article);

        Comment comment = createTestComment();
        comment.setArticle(article);
        comment.setUser(user);
        entityManager.persist(comment);

        CommentLike commentLike = createTestCommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        entityManager.persist(commentLike);

        CommentLikeRequestDTO commentLikeRequestDTO = new CommentLikeRequestDTO();
        commentLikeRequestDTO.setCommentId(comment.getId());
        commentLikeRequestDTO.setUserId(user.getId());

        entityManager.flush();

        Long commentId = comment.getId();
        Long userId = user.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/like")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(commentLikeRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }


    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
