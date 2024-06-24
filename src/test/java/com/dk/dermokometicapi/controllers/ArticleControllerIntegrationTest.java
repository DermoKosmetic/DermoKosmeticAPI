package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.ArticleLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.ArticleRequestDTO;
import com.dk.dermokometicapi.models.dto.FilterRequestDTO;
import com.dk.dermokometicapi.models.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ArticleControllerIntegrationTest {

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

    private ArticleRequestDTO createTestArticleRequestDTO() {
        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        articleRequestDTO.setTitle("Title X");
        articleRequestDTO.setDescription("Description X");
        articleRequestDTO.setContent("Content X");
        articleRequestDTO.setMainImg("Img X");
        articleRequestDTO.setType("Type X");
        return articleRequestDTO;
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("Username X");
        user.setEmail("Email X");
        user.setPassword("Password X");
        user.setProfilePic("Pic X");
        return user;
    }

    @Test
    public void testGetAllArticles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetFullArticleById() throws Exception {
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
        entityManager.flush();

        Long articleId = article.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/articles/id/{id}", articleId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetFullArticleByTitle() throws Exception {
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
        entityManager.flush();

        String articleTitle = article.getTitle();

        mockMvc.perform(MockMvcRequestBuilders.get("/articles/title/{title}", articleTitle))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSearchArticles() throws Exception {
        FilterRequestDTO requestDTO = new FilterRequestDTO();

        requestDTO.setPageSize(5);
        requestDTO.setPageNum(5);
        requestDTO.setCategories(Arrays.asList("Type1", "Type2"));
        requestDTO.setOrderBy("likes");

        mockMvc.perform(MockMvcRequestBuilders.post("/articles/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testCreateArticle() throws Exception {
        ArticleRequestDTO articleRequestDTO = createTestArticleRequestDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(articleRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testLikeArticle() throws Exception {
        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        entityManager.persist(article);

        User user = createTestUser();
        entityManager.persist(user);

        entityManager.flush();

        Long articleId = article.getId();
        Long userId = user.getId();

        ArticleLikeRequestDTO likeRequestDTO = new ArticleLikeRequestDTO();
        likeRequestDTO.setArticleId(articleId);
        likeRequestDTO.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/articles/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(likeRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testDislikeArticle() throws Exception {
        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        entityManager.persist(article);

        User user = createTestUser();
        entityManager.persist(user);

        Long articleId = article.getId();
        Long userId = user.getId();

        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticle(article);
        articleLike.setUser(user);
        articleLike.setLikeDate(LocalDate.now());
        entityManager.persist(articleLike);

        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.delete("/articles/like")
                        .param("articleId", String.valueOf(articleId))
                        .param("userId", String.valueOf(userId)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteArticleById() throws Exception {
        ArticleDetail articleDetail = createTestArticleDetail();
        entityManager.persist(articleDetail);

        Article article = createTestArticle();
        article.setArticleDetail(articleDetail);
        entityManager.persist(article);
        entityManager.flush();

        Long articleId = article.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/articles/id/{id}", articleId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testGetTypes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles/types"))
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
