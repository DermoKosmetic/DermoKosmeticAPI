package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.AnswerLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.AnswerRequestDTO;
import com.dk.dermokometicapi.models.dto.AnswerResponseDTO;
import com.dk.dermokometicapi.models.dto.ListRequestDTO;
import com.dk.dermokometicapi.models.entities.Answer;
import com.dk.dermokometicapi.models.entities.AnswerLike;
import com.dk.dermokometicapi.models.entities.Question;
import com.dk.dermokometicapi.models.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AnswerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private User createTestUser() {
        User user = new User();
        user.setUsername("Username X");
        user.setEmail("Email X");
        user.setPassword("Password X");
        user.setProfilePic("Pic X");
        return user;
    }

    private Question createTestQuestion() {
        Question question = new Question();
        question.setTitle("Title X");
        question.setContent("Content X");
        question.setType("Type X");
        question.setPublicationDate(LocalDate.now());
        return question;
    }

    private Answer createTestAnswer() {
        Answer answer = new Answer();
        answer.setContent("Answer X");
        answer.setPublicationDate(LocalDate.now());
        return answer;
    }

    private AnswerRequestDTO createTestAnswerRequestDTO() {
        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO();
        answerRequestDTO.setContent("Answer X");
        return answerRequestDTO;
    }

    @Test
    public void testGetAllAnswers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/answers"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetAnswerById() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        Answer answer = createTestAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        entityManager.persist(answer);

        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.get("/answers/" + answer.getId()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void testCreateAnswer() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        entityManager.flush();

        AnswerRequestDTO answerResponseDTO = createTestAnswerRequestDTO();
        answerResponseDTO.setUserId(user.getId());
        answerResponseDTO.setQuestionId(question.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/answers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(answerResponseDTO)))
            .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void testGetAnswersByQuestionId() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        entityManager.flush();

        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageNum(0);
        listRequestDTO.setPageSize(10);
        listRequestDTO.setOrderBy("likes");

        mockMvc.perform(MockMvcRequestBuilders.get("/answers/question/" + question.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(listRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetAnswerByParentId() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        Answer parentAnswer = createTestAnswer();
        parentAnswer.setUser(user);
        parentAnswer.setQuestion(question);
        entityManager.persist(parentAnswer);

        entityManager.flush();

        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageNum(0);
        listRequestDTO.setPageSize(10);
        listRequestDTO.setOrderBy("likes");

        mockMvc.perform(MockMvcRequestBuilders.get("/answers/parent/" + parentAnswer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(listRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void testDeleteAnswer() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        Answer answer = createTestAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        entityManager.persist(answer);

        entityManager.flush();

        Long answerId = answer.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/answers/" + answerId))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    public void testLikeAnswer() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        Answer answer = createTestAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        entityManager.persist(answer);

        entityManager.flush();

        Long answerId = answer.getId();
        Long userId = user.getId();

        AnswerLikeRequestDTO likeRequestDTO = new AnswerLikeRequestDTO();
        likeRequestDTO.setAnswerId(answerId);
        likeRequestDTO.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/answers/like")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(likeRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUnlikeAnswer() throws Exception {
        User user = createTestUser();
        entityManager.persist(user);

        Question question = createTestQuestion();
        question.setUser(user);
        entityManager.persist(question);

        Answer answer = createTestAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        entityManager.persist(answer);

        AnswerLike answerLike = new AnswerLike();
        answerLike.setAnswer(answer);
        answerLike.setUser(user);
        answerLike.setLikeDate(LocalDate.now());
        entityManager.persist(answerLike);

        entityManager.flush();

        Long answerId = answer.getId();
        Long userId = user.getId();

        AnswerLikeRequestDTO likeRequestDTO = new AnswerLikeRequestDTO();
        likeRequestDTO.setAnswerId(answerId);
        likeRequestDTO.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/answers/like")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(likeRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
