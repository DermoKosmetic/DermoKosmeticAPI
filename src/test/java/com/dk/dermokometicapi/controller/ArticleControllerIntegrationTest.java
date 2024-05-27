package com.dk.dermokometicapi.controller;

import com.dk.dermokometicapi.models.dto.ArticleLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.ArticleRequestDTO;
import com.dk.dermokometicapi.models.dto.FilterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ArticleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllArticles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetFullArticleById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles/id/{id}", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetFullArticleByTitle() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles/title/{title}", "Title 1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSearchArticles() throws Exception {
        FilterRequestDTO requestDTO = new FilterRequestDTO();

        requestDTO.setPageSize(5);
        requestDTO.setPageNum(5);
        requestDTO.setCategories(Arrays.asList("Type1", "Type2"));
        requestDTO.setOrderBy("likes");

        mockMvc.perform(MockMvcRequestBuilders.get("/articles/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testCreateArticle() throws Exception {
        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();

        articleRequestDTO.setTitle("Title3");
        articleRequestDTO.setDescription("Description 2");
        articleRequestDTO.setContent("Content 2");
        articleRequestDTO.setMainImg("Img 2");
        articleRequestDTO.setType("Type2");

        mockMvc.perform(MockMvcRequestBuilders.post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(articleRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testLikeArticle() throws Exception {

        ArticleLikeRequestDTO likeRequestDTO = new ArticleLikeRequestDTO();
        likeRequestDTO.setArticleId(1L);
        likeRequestDTO.setUserId(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/articles/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(likeRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDislikeArticle() throws Exception {

        ArticleLikeRequestDTO likeRequestDTO = new ArticleLikeRequestDTO();
        likeRequestDTO.setArticleId(1L);
        likeRequestDTO.setUserId(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/articles/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(likeRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteArticleById() throws Exception {
        Long articleId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/articles/id/{id}", articleId))
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
