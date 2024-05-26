package com.dk.dermokometicapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.mappers.AnswerMapper;
import com.dk.dermokometicapi.models.dto.AnswerRequestDTO;
import com.dk.dermokometicapi.models.dto.AnswerResponseDTO;
import com.dk.dermokometicapi.models.entities.Answer;
import com.dk.dermokometicapi.models.entities.Question;
import com.dk.dermokometicapi.models.entities.User;
import com.dk.dermokometicapi.repositories.AnswerLikeRepository;
import com.dk.dermokometicapi.repositories.AnswerRepository;
import com.dk.dermokometicapi.repositories.QuestionRepository;
import com.dk.dermokometicapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {
    @Mock
    private AnswerLikeRepository answerLikeRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerMapper answerMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    public AnswerService answerService;

    @Test
    public void testConvertToDTO() {
        // Arrange
        Answer answer = new Answer();
        answer.setId(1L);

        long likes = 10L;
        long answersCount = 5L;

        AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();
        answerResponseDTO.setId(answer.getId());
        answerResponseDTO.setLikes(likes);

        when(answerLikeRepository.countByAnswer(answer)).thenReturn(likes);
        when(answerRepository.countByParentAnswer(answer)).thenReturn(answersCount);
        when(answerMapper.convertToDTO(answer, likes, answersCount)).thenReturn(answerResponseDTO);

        // Act
        AnswerResponseDTO result = answerService.convertToDTO(answer);

        // Assert
        assertNotNull(result);
        assertEquals(answerResponseDTO.getId(), result.getId());
        assertEquals(answerResponseDTO.getLikes(), result.getLikes());

        // Verify interactions
        verify(answerLikeRepository, times(1)).countByAnswer(answer);
        verify(answerRepository, times(1)).countByParentAnswer(answer);
        verify(answerMapper, times(1)).convertToDTO(answer, likes, answersCount);
    }

    @Test
    public void testGetAllAnswers() {
        // Arrange
        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        List<Answer> answers = Arrays.asList(answer1, answer2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());

        when(answerRepository.findAll()).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);
        when(answerLikeRepository.countByAnswer(answer1)).thenReturn(0L);
        when(answerRepository.countByParentAnswer(answer1)).thenReturn(0L);
        when(answerLikeRepository.countByAnswer(answer2)).thenReturn(0L);
        when(answerRepository.countByParentAnswer(answer2)).thenReturn(0L);

        // Act
        List<AnswerResponseDTO> result = answerService.getAllAnswers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        // Verify interactions
        verify(answerRepository, times(1)).findAll();
        verify(answerLikeRepository, times(1)).countByAnswer(answer1);
        verify(answerRepository, times(1)).countByParentAnswer(answer1);
        verify(answerLikeRepository, times(1)).countByAnswer(answer2);
        verify(answerRepository, times(1)).countByParentAnswer(answer2);
        verify(answerMapper, times(1)).convertToDTO(answer1, 0L, 0L);
        verify(answerMapper, times(1)).convertToDTO(answer2, 0L, 0L);
    }

    @Test
    public void testGetAnswerById() {
        // Arrange
        Long answerId = 1L;
        Answer answer = new Answer();
        answer.setId(answerId);

        Long likes = 10L;
        Long answersCount = 5L;

        AnswerResponseDTO dto = new AnswerResponseDTO();
        dto.setId(answer.getId());

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(answerLikeRepository.countByAnswer(answer)).thenReturn(likes);
        when(answerRepository.countByParentAnswer(answer)).thenReturn(answersCount);
        when(answerMapper.convertToDTO(answer, likes, answersCount)).thenReturn(dto);

        // Act
        AnswerResponseDTO result = answerService.getAnswerById(answerId);

        // Assert
        assertNotNull(result);
        assertEquals(dto, result);

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
        verify(answerLikeRepository, times(1)).countByAnswer(answer);
        verify(answerRepository, times(1)).countByParentAnswer(answer);
        verify(answerMapper, times(1)).convertToDTO(answer, likes, answersCount);
    }

    @Test
    public void testGetAnswerById_AnswerNotFound() {
        // Arrange
        Long answerId = 1L;
        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.getAnswerById(answerId);
        });

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
        verify(answerLikeRepository, never()).countByAnswer(any());
        verify(answerRepository, never()).countByParentAnswer(any());
        verify(answerMapper, never()).convertToDTO(any(), anyLong(), anyLong());
    }

    @Test
    public void testAddAnswer() {
        // Arrange
        Long userId = 1L;
        Long questionId = 1L;
        Long parentAnswerId = 2L;

        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO();
        answerRequestDTO.setUserId(userId);
        answerRequestDTO.setQuestionId(questionId);
        answerRequestDTO.setParentAnswerId(parentAnswerId);

        User user = new User();
        user.setId(userId);

        Question question = new Question();
        question.setId(questionId);

        Answer parentAnswer = new Answer();
        parentAnswer.setId(parentAnswerId);

        Answer answer = new Answer();
        AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerRepository.findById(parentAnswerId)).thenReturn(Optional.of(parentAnswer));
        when(answerMapper.convertToEntity(answerRequestDTO, user, question, parentAnswer)).thenReturn(answer);
        when(answerRepository.save(answer)).thenReturn(answer);
        when(answerService.convertToDTO(answer)).thenReturn(answerResponseDTO);

        // Act
        AnswerResponseDTO result = answerService.addAnswer(answerRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(answerResponseDTO, result);

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verify(questionRepository, times(1)).findById(questionId);
        verify(answerRepository, times(1)).findById(parentAnswerId);
        verify(answerMapper, times(1)).convertToEntity(answerRequestDTO, user, question, parentAnswer);
        verify(answerRepository, times(1)).save(answer);
    }

    @Test
    public void testAddAnswer_UserNotFound() {
        // Arrange
        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO();
        answerRequestDTO.setUserId(1L);
        when(userRepository.findById(answerRequestDTO.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.addAnswer(answerRequestDTO);
        });

        // Verify interactions
        verify(userRepository, times(1)).findById(answerRequestDTO.getUserId());
        verify(questionRepository, never()).findById(anyLong());
        verify(answerRepository, never()).findById(anyLong());
        verify(answerMapper, never()).convertToEntity(any(), any(), any(), any());
        verify(answerRepository, never()).save(any());
    }

    @Test
    public void testAddAnswer_QuestionNotFound() {
        // Arrange
        Long userId = 1L;
        Long questionId = 1L;

        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO();
        answerRequestDTO.setUserId(userId);
        answerRequestDTO.setQuestionId(questionId);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.addAnswer(answerRequestDTO);
        });

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verify(questionRepository, times(1)).findById(questionId);
        verify(answerRepository, never()).findById(anyLong());
        verify(answerMapper, never()).convertToEntity(any(), any(), any(), any());
        verify(answerRepository, never()).save(any());
    }

    @Test
    public void testAddAnswer_ParentAnswerNotFound() {
        // Arrange
        Long userId = 1L;
        Long questionId = 1L;
        Long parentAnswerId = 2L;

        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO();
        answerRequestDTO.setUserId(userId);
        answerRequestDTO.setQuestionId(questionId);
        answerRequestDTO.setParentAnswerId(parentAnswerId);

        User user = new User();
        user.setId(userId);

        Question question = new Question();
        question.setId(questionId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerRepository.findById(parentAnswerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.addAnswer(answerRequestDTO);
        });

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verify(questionRepository, times(1)).findById(questionId);
        verify(answerRepository, times(1)).findById(parentAnswerId);
        verify(answerMapper, never()).convertToEntity(any(), any(), any(), any());
        verify(answerRepository, never()).save(any());
    }



}
