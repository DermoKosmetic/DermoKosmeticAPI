package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.FilterRequestDTO;
import com.dk.dermokometicapi.models.dto.QuestionLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.QuestionRequestDTO;
import com.dk.dermokometicapi.models.entities.Question;
import com.dk.dermokometicapi.models.entities.QuestionLike;
import com.dk.dermokometicapi.models.entities.User;
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
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class QuestionControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;


    private Question createTestQuestion(){
        Question question = new Question();
        question.setTitle("Title X");
        question.setContent("This is suppose to be the content of this question.");
        question.setType("Type X");
        question.setPublicationDate(LocalDate.now());
        return question;
    }

    private QuestionRequestDTO createTestQuestionRequestDTO(){
        QuestionRequestDTO questionRequestDTO = new QuestionRequestDTO();
        questionRequestDTO.setTitle("Title X");
        questionRequestDTO.setContent("This is suppose to be the content of this question.");
        questionRequestDTO.setType("Type X");
        return questionRequestDTO;
    }

    private User createTestUser(){
        User user = new User();
        user.setUsername("Username X");
        user.setEmail("Email X");
        user.setPassword("Password X");
        user.setProfilePic("Pic X");
        return user;
    }

    @Test
    public void testGetAllQuestions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/questions"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateQuestion() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);
        entityManager.flush();

        QuestionRequestDTO questionRequestDTO = createTestQuestionRequestDTO();
        questionRequestDTO.setUserId(user.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(questionRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetQuestionById() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);
        entityManager.flush();

        Long questionId = question.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/questions/id/{id}", questionId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetQuestionByTitle() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);
        entityManager.flush();

        String questionTitle = question.getTitle();
        mockMvc.perform(MockMvcRequestBuilders.get("/questions/title/{title}", questionTitle))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteQuestionById() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);
        entityManager.flush();
        Long questionId = question.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/questions/id/{id}", questionId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testLikeQuestion() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);
        entityManager.flush();

        QuestionLikeRequestDTO questionLikeRequestDTO = new QuestionLikeRequestDTO();
        questionLikeRequestDTO.setUserId(user.getId());
        questionLikeRequestDTO.setQuestionId(question.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/questions/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(questionLikeRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testDeleteLikeQuestion() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        QuestionLike questionLike = new QuestionLike();
        questionLike.setLikeDate(LocalDate.now());
        questionLike.setQuestion(question);
        questionLike.setUser(user);
        entityManager.persist(questionLike);

        QuestionLikeRequestDTO questionLikeRequestDTO = new QuestionLikeRequestDTO();
        questionLikeRequestDTO.setUserId(user.getId());
        questionLikeRequestDTO.setQuestionId(question.getId());

        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.delete("/questions/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(questionLikeRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testSearchQuestion() throws Exception {
        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setCategories(Arrays.asList("Type1", "Type2"));
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(5);
        filterRequestDTO.setOrderBy("likes");

        mockMvc.perform(MockMvcRequestBuilders.post("/questions/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(filterRequestDTO)))
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
