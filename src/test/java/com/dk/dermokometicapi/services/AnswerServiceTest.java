package com.dk.dermokometicapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.mappers.AnswerLikeMapper;
import com.dk.dermokometicapi.mappers.AnswerMapper;
import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.Answer;
import com.dk.dermokometicapi.models.entities.AnswerLike;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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

    @Mock
    private AnswerLikeMapper answerLikeMapper;

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
    public void testAddAnswer_ParentAnswerAvoided(){
        // Arrange
        Long userId = 1L;
        Long questionId = 1L;

        AnswerRequestDTO answerRequestDTO = new AnswerRequestDTO();
        answerRequestDTO.setUserId(userId);
        answerRequestDTO.setQuestionId(questionId);

        User user = new User();
        user.setId(userId);

        Question question = new Question();
        question.setId(questionId);


        Answer answer = new Answer();
        AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerMapper.convertToEntity(answerRequestDTO, user, question, null)).thenReturn(answer);
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
        verify(answerMapper, times(1)).convertToEntity(answerRequestDTO, user, question, null);
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

    @Test
    public void testGetAnswersByQuestionId_likes() {
        // Arrange
        Long questionId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("likes");

        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        Pageable pageable = Pageable.ofSize(2).withPage(0);

        Page<Answer> answers = new PageImpl<>(Arrays.asList(answer1, answer2), pageable, 2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());
        dto1.setLikes(0L);
        dto1.setResponses(0L);

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());
        dto2.setLikes(0L);
        dto2.setResponses(0L);

        Page<AnswerResponseDTO> expected = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        when(answerRepository.findLikedByQuestionId(questionId, pageable)).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);

        // Act
        Page<AnswerResponseDTO> result = answerService.getAnswersByQuestionId(questionId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);

        // Verify interactions
        verify(answerRepository, times(1)).findLikedByQuestionId(questionId, pageable);
    }

    @Test
    public void testGetAnswersByQuestionId_responses() {
        // Arrange
        Long questionId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("responses");

        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        Pageable pageable = Pageable.ofSize(2).withPage(0);

        Page<Answer> answers = new PageImpl<>(Arrays.asList(answer1, answer2), pageable, 2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());
        dto1.setLikes(0L);
        dto1.setResponses(0L);

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());
        dto2.setLikes(0L);
        dto2.setResponses(0L);

        Page<AnswerResponseDTO> expected = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        when(answerRepository.findAnsweredByQuestionId(questionId, pageable)).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);

        // Act
        Page<AnswerResponseDTO> result = answerService.getAnswersByQuestionId(questionId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);

        // Verify interactions
        verify(answerRepository, times(1)).findAnsweredByQuestionId(questionId, pageable);
    }

    @Test
    public void testGetAnswersByQuestionId_default() {
        // Arrange
        Long questionId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("default");

        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        Pageable pageable = Pageable.ofSize(2).withPage(0);

        Page<Answer> answers = new PageImpl<>(Arrays.asList(answer1, answer2), pageable, 2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());
        dto1.setLikes(0L);
        dto1.setResponses(0L);

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());
        dto2.setLikes(0L);
        dto2.setResponses(0L);

        Page<AnswerResponseDTO> expected = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        when(answerRepository.findRecentByQuestionId(questionId, pageable)).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);

        // Act
        Page<AnswerResponseDTO> result = answerService.getAnswersByQuestionId(questionId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);

        // Verify interactions
        verify(answerRepository, times(1)).findRecentByQuestionId(questionId, pageable);
    }

    @Test
    public void testGetAnswersByParentId_likes() {
        // Arrange
        Long parentAnswerId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("likes");

        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        Pageable pageable = Pageable.ofSize(2).withPage(0);

        Page<Answer> answers = new PageImpl<>(Arrays.asList(answer1, answer2), pageable, 2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());
        dto1.setLikes(0L);
        dto1.setResponses(0L);

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());
        dto2.setLikes(0L);
        dto2.setResponses(0L);

        Page<AnswerResponseDTO> expected = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        when(answerRepository.findLikedByParentAnswerId(parentAnswerId, pageable)).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);

        // Act
        Page<AnswerResponseDTO> result = answerService.getAnswersByParentId(parentAnswerId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);

        // Verify interactions
        verify(answerRepository, times(1)).findLikedByParentAnswerId(parentAnswerId, pageable);
    }

    @Test
    public void testGetAnswersByParentId_responses() {
        // Arrange
        Long parentAnswerId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("responses");

        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        Pageable pageable = Pageable.ofSize(2).withPage(0);

        Page<Answer> answers = new PageImpl<>(Arrays.asList(answer1, answer2), pageable, 2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());
        dto1.setLikes(0L);
        dto1.setResponses(0L);

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());
        dto2.setLikes(0L);
        dto2.setResponses(0L);

        Page<AnswerResponseDTO> expected = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        when(answerRepository.findCommentedByParentAnswerId(parentAnswerId, pageable)).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);

        // Act
        Page<AnswerResponseDTO> result = answerService.getAnswersByParentId(parentAnswerId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);

        // Verify interactions
        verify(answerRepository, times(1)).findCommentedByParentAnswerId(parentAnswerId, pageable);
    }

    @Test
    public void testGetAnswersByParentId_default() {
        // Arrange
        Long parentAnswerId = 1L;
        ListRequestDTO listRequestDTO = new ListRequestDTO();
        listRequestDTO.setPageSize(2);
        listRequestDTO.setPageNum(0);
        listRequestDTO.setOrderBy("default");

        Answer answer1 = new Answer();
        answer1.setId(1L);

        Answer answer2 = new Answer();
        answer2.setId(2L);

        Pageable pageable = Pageable.ofSize(2).withPage(0);

        Page<Answer> answers = new PageImpl<>(Arrays.asList(answer1, answer2), pageable, 2);

        AnswerResponseDTO dto1 = new AnswerResponseDTO();
        dto1.setId(answer1.getId());
        dto1.setLikes(0L);
        dto1.setResponses(0L);

        AnswerResponseDTO dto2 = new AnswerResponseDTO();
        dto2.setId(answer2.getId());
        dto2.setLikes(0L);
        dto2.setResponses(0L);

        Page<AnswerResponseDTO> expected = new PageImpl<>(Arrays.asList(dto1, dto2), pageable, 2);

        when(answerRepository.findRecentByParentCommentId(parentAnswerId, pageable)).thenReturn(answers);
        when(answerMapper.convertToDTO(answer1, 0L, 0L)).thenReturn(dto1);
        when(answerMapper.convertToDTO(answer2, 0L, 0L)).thenReturn(dto2);

        // Act
        Page<AnswerResponseDTO> result = answerService.getAnswersByParentId(parentAnswerId, listRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);

        // Verify interactions
        verify(answerRepository, times(1)).findRecentByParentCommentId(parentAnswerId, pageable);
    }

    @Test
    public void testDeleteAnswer() {
        // Arrange
        Long answerId = 1L;
        Answer answer = new Answer();
        answer.setId(answerId);

        when(answerRepository.existsById(answerId)).thenReturn(true);

        // Act
        answerService.deleteAnswer(answerId);

        // Verify interactions
        verify(answerRepository, times(1)).existsById(answerId);
        verify(answerRepository, times(1)).deleteById(answerId);
    }

    @Test
    public void testDeleteAnswer_AnswerNotFound() {
        // Arrange
        Long answerId = 1L;
        when(answerRepository.existsById(answerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.deleteAnswer(answerId);
        });

        // Verify interactions
        verify(answerRepository, times(1)).existsById(answerId);
        verify(answerRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testLikeAnswer() {
        // Arrange
        Long answerId = 1L;
        Long userId = 1L;

        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        Answer answer = new Answer();
        answer.setId(answerId);

        User user = new User();
        user.setId(userId);

        AnswerLike answerLike = new AnswerLike();
        answerLike.setAnswer(answer);
        answerLike.setUser(user);
        answerLike.setLikeDate(LocalDate.now());

        AnswerLikeResponseDTO answerLikeResponseDTO = new AnswerLikeResponseDTO();
        answerLikeResponseDTO.setId(1L);
        answerLikeResponseDTO.setAnswerId(answerId);
        answerLikeResponseDTO.setUserId(userId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(answerLikeRepository.existsByAnswerAndUser(answer, user)).thenReturn(false);
        when(answerLikeRepository.save(answerLike)).thenReturn(answerLike);
        when(answerLikeMapper.convertToDTO(answerLike)).thenReturn(answerLikeResponseDTO);

        // Act
        AnswerLikeResponseDTO result = answerService.likeAnswer(answerLikeRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(answerLikeResponseDTO, result);

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
        verify(userRepository, times(1)).findById(userId);
        verify(answerLikeRepository, times(1)).existsByAnswerAndUser(answer, user);
        verify(answerLikeRepository, times(1)).save(answerLike);
        verify(answerLikeMapper, times(1)).convertToDTO(answerLike);
    }

    @Test
    public void testLikeAnswer_AnswerNotFound() {
        // Arrange
        Long answerId = 1L;
        Long userId = 2L;
        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.likeAnswer(answerLikeRequestDTO);
        });

        // Verify interactions

        verify(answerRepository, times(1)).findById(answerId);
    }

    @Test
    public void testLikeAnswer_UserNotFound() {
        // Arrange
        Long answerId = 1L;
        Long userId = 2L;
        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        Answer answer = new Answer();
        answer.setId(answerId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.likeAnswer(answerLikeRequestDTO);
        });

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testLikeAnswer_UserAlreadyLiked() {
        // Arrange
        Long answerId = 1L;
        Long userId = 2L;
        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        Answer answer = new Answer();
        answer.setId(answerId);

        User user = new User();
        user.setId(userId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(answerLikeRepository.existsByAnswerAndUser(answer, user)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            answerService.likeAnswer(answerLikeRequestDTO);
        });

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
        verify(userRepository, times(1)).findById(userId);
        verify(answerLikeRepository, times(1)).existsByAnswerAndUser(answer, user);
    }

    @Test
    public void testUnlikeAnswer() {
        // Arrange
        Long answerId = 1L;
        Long userId = 1L;

        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        Answer answer = new Answer();
        answer.setId(answerId);

        User user = new User();
        user.setId(userId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(answerLikeRepository.existsByAnswerAndUser(answer, user)).thenReturn(true);

        // Act
        answerService.unlikeAnswer(answerLikeRequestDTO);

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
        verify(userRepository, times(1)).findById(userId);
        verify(answerLikeRepository, times(1)).existsByAnswerAndUser(answer, user);
        verify(answerLikeRepository, times(1)).deleteByAnswerAndUser(answer, user);
    }

    @Test
    public void testUnlikeAnswer_AnswerNotFound() {
        // Arrange
        Long answerId = 1L;
        Long userId = 2L;
        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.unlikeAnswer(answerLikeRequestDTO);
        });

        // Verify interactions
        verify(answerRepository, times(1)).findById(answerId);
    }

    @Test
    public void testUnlikeAnswer_UserNotFound() {
        // Arrange
        Long answerId = 1L;
        Long userId = 2L;
        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        Answer answer = new Answer();
        answer.setId(answerId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(userRepository.findById(userId)).thenReturn((Optional.empty()));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.unlikeAnswer(answerLikeRequestDTO);
        });

        // Verify interactions

        verify(answerRepository, times(1)).findById(answerId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUnlikeAnswer_UserLikeNotFound(){
        // Arrange
        Long answerId = 1L;
        Long userId = 2L;
        AnswerLikeRequestDTO answerLikeRequestDTO = new AnswerLikeRequestDTO();
        answerLikeRequestDTO.setAnswerId(answerId);
        answerLikeRequestDTO.setUserId(userId);

        Answer answer = new Answer();
        answer.setId(answerId);

        User user = new User();
        user.setId(userId);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(answerLikeRepository.existsByAnswerAndUser(answer, user)).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            answerService.unlikeAnswer(answerLikeRequestDTO);
        });

        // Verify interactions

        verify(answerRepository, times(1)).findById(answerId);
        verify(userRepository, times(1)).findById(userId);
        verify(answerLikeRepository, times(1)).existsByAnswerAndUser(answer, user);
    }


}
