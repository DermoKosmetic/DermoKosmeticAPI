package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.*;
import com.dk.dermokometicapi.exceptions.*;
import com.dk.dermokometicapi.mappers.*;
import com.dk.dermokometicapi.repositories.*;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private QuestionLikeMapper questionLikeMapper;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionLikeRepository questionLikeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private UserService userService;

    @Test
    public void testCreateQuestion() {
        //Arrange
        User user = new User();
        user.setId(1L);

        QuestionRequestDTO questionRequestDTO = new QuestionRequestDTO();
        questionRequestDTO.setTitle("Title");
        questionRequestDTO.setContent("Content");
        questionRequestDTO.setType("type");
        questionRequestDTO.setUserId(1L);

        Question question = new Question();
        question.setId(1L);
        question.setTitle(questionRequestDTO.getTitle());
        question.setContent(questionRequestDTO.getContent());
        question.setType(questionRequestDTO.getType());
        question.setPublicationDate(LocalDate.now());
        question.setUser(user);

        QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO();
        questionResponseDTO.setId(question.getId());
        questionResponseDTO.setTitle(question.getTitle());
        questionResponseDTO.setContent(question.getContent());
        questionResponseDTO.setType(question.getType());
        questionResponseDTO.setUserId(question.getUser().getId());

        when(questionRepository.existsByTitle(questionRequestDTO.getTitle())).thenReturn(false);
        when(questionMapper.convertToEntity(questionRequestDTO)).thenReturn(question);
        when(questionMapper.convertToDTO(question, 0L, 0L)).thenReturn(questionResponseDTO);

        //Act
        QuestionResponseDTO result = questionService.createQuestion(questionRequestDTO);

        //Assert
        assertNotNull(result);
        assertEquals(questionResponseDTO.getId(), result.getId());
        assertEquals(questionResponseDTO.getTitle(), result.getTitle());
        assertEquals(questionResponseDTO.getContent(), result.getContent());
        assertEquals(questionResponseDTO.getType(), result.getType());

        //verify
        verify(questionRepository, times(1)).existsByTitle(questionRequestDTO.getTitle());
        verify(questionMapper, times(1)).convertToEntity(questionRequestDTO);
        verify(questionMapper, times(1)).convertToDTO(question, 0L, 0L);
        verify(questionRepository, times(1)).save(question);

    }

    @Test
    public void testCreateQuestion_QuestionAlreadyExists() {
        // Arrange
        QuestionRequestDTO questionRequestDTO = new QuestionRequestDTO();
        questionRequestDTO.setTitle("Title");
        questionRequestDTO.setContent("Content");
        questionRequestDTO.setType("type");
        questionRequestDTO.setUserId(1L);

        when(questionRepository.existsByTitle(questionRequestDTO.getTitle())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(BadRequestException.class, () -> questionService.createQuestion(questionRequestDTO));

        String expectedMessage = "Question with title: " + questionRequestDTO.getTitle() + " already exists";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        // Verify that no other interactions happen
        verify(questionRepository, times(1)).existsByTitle(questionRequestDTO.getTitle());
        verify(questionRepository, never()).save(any(Question.class));
        verify(questionMapper, never()).convertToEntity(any(QuestionRequestDTO.class));
        verify(questionMapper, never()).convertToDTO(any(Question.class), anyLong(), anyLong());
    }

    @Test
    public void testGetAllQuestions() {
        // Arrange
        Question question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Title1");
        question1.setContent("Content1");
        question1.setType("type1");
        question1.setPublicationDate(LocalDate.now());

        Question question2 = new Question();
        question2.setId(2L);
        question2.setTitle("Title2");
        question2.setContent("Content2");
        question2.setType("type2");
        question2.setPublicationDate(LocalDate.now());

        List<Question> questionList = Arrays.asList(question1, question2);

        Long likes1 = 10L;
        Long answers1 = 5L;
        Long likes2 = 20L;
        Long answers2 = 15L;

        QuestionResponseDTO dto1 = new QuestionResponseDTO();
        dto1.setId(question1.getId());
        dto1.setTitle(question1.getTitle());
        dto1.setContent(question1.getContent());
        dto1.setType(question1.getType());
        dto1.setLikes(likes1);
        dto1.setAnswers(answers1);

        QuestionResponseDTO dto2 = new QuestionResponseDTO();
        dto2.setId(question2.getId());
        dto2.setTitle(question2.getTitle());
        dto2.setContent(question2.getContent());
        dto2.setType(question2.getType());
        dto2.setLikes(likes2);
        dto2.setAnswers(answers2);

        when(questionRepository.findAll()).thenReturn(questionList);
        when(questionLikeRepository.countByQuestion(question1)).thenReturn(likes1);
        when(questionLikeRepository.countByQuestion(question2)).thenReturn(likes2);
        when(questionRepository.countAnswersByQuestion(question1)).thenReturn(answers1);
        when(questionRepository.countAnswersByQuestion(question2)).thenReturn(answers2);

        when(questionMapper.convertToDTO(question1, likes1, answers1)).thenReturn(dto1);
        when(questionMapper.convertToDTO(question2, likes2, answers2)).thenReturn(dto2);

        // Act
        List<QuestionResponseDTO> result = questionService.getAllQuestions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    public void testGetQuestionById(){
        // Arrange
        Long questionId = 1L;
        Question question = new Question();
        question.setId(questionId);
        question.setTitle("Title");
        question.setContent("Content");
        question.setType("Type");
        question.setPublicationDate(LocalDate.now());

        Long likes = 10L;
        Long answers = 5L;

        QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO();
        questionResponseDTO.setId(questionId);
        questionResponseDTO.setTitle("Title");
        questionResponseDTO.setContent("Content");
        questionResponseDTO.setType("Type");
        questionResponseDTO.setLikes(likes);
        questionResponseDTO.setAnswers(answers);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionLikeRepository.countByQuestion(question)).thenReturn(likes);
        when(questionRepository.countAnswersByQuestion(question)).thenReturn(answers);
        when(questionMapper.convertToDTO(question, likes, answers)).thenReturn(questionResponseDTO);

        // Act
        QuestionResponseDTO result = questionService.getQuestionById(questionId);

        // Assert
        assertNotNull(result);
        assertEquals(questionResponseDTO.getId(), result.getId());
        assertEquals(questionResponseDTO.getTitle(), result.getTitle());
        assertEquals(questionResponseDTO.getContent(), result.getContent());
        assertEquals(questionResponseDTO.getType(), result.getType());
        assertEquals(questionResponseDTO.getLikes(), result.getLikes());
        assertEquals(questionResponseDTO.getAnswers(), result.getAnswers());
    }

    @Test
    public void testGetQuestionById_NotFound() {
        // Arrange
        Long questionId = 1L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> questionService.getQuestionById(questionId));

        assertEquals("Question with id: " + questionId + " not found", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findById(questionId);
        verify(questionRepository, never()).findQuestionLikesById(anyLong());
        verify(questionRepository, never()).findQuestionAnswersById(anyLong());
        verify(questionMapper, never()).convertToDTO(any(), anyLong(), anyLong());
    }

    @Test
    public void testGetQuestionByTitle() {
        // Arrange
        String title = "Title";
        Long questionId = 1L;
        Question question = new Question();
        question.setId(questionId);
        question.setTitle(title);
        question.setContent("Content");
        question.setType("Type");
        question.setPublicationDate(LocalDate.now());

        Long likes = 10L;
        Long answers = 5L;

        QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO();
        questionResponseDTO.setId(questionId);
        questionResponseDTO.setTitle(title);
        questionResponseDTO.setContent("Content");
        questionResponseDTO.setType("Type");
        questionResponseDTO.setLikes(likes);
        questionResponseDTO.setAnswers(answers);

        when(questionRepository.findByTitle(title)).thenReturn(Optional.of(question));
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(likes);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(answers);
        when(questionMapper.convertToDTO(question, likes, answers)).thenReturn(questionResponseDTO);

        // Act
        QuestionResponseDTO result = questionService.getQuestionByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(questionResponseDTO.getId(), result.getId());
        assertEquals(questionResponseDTO.getTitle(), result.getTitle());
        assertEquals(questionResponseDTO.getContent(), result.getContent());
        assertEquals(questionResponseDTO.getType(), result.getType());
        assertEquals(questionResponseDTO.getLikes(), result.getLikes());
        assertEquals(questionResponseDTO.getAnswers(), result.getAnswers());
    }

    @Test
    public void testGetQuestionByTitle_NotFound() {
        // Arrange
        String title = "Title";
        when(questionRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> questionService.getQuestionByTitle(title));

        assertEquals("Question with title: " + title + " not found", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findByTitle(title);
        verify(questionRepository, never()).findQuestionLikesById(anyLong());
        verify(questionRepository, never()).findQuestionAnswersById(anyLong());
        verify(questionMapper, never()).convertToDTO(any(), anyLong(), anyLong());
    }

    @Test
    public void testDeleteQuestionById() {
        // Arrange
        Long questionId = 1L;

        when(questionRepository.existsById(questionId)).thenReturn(true);

        // Act
        questionService.deleteQuestionById(questionId);

        // Assert
        verify(questionRepository, times(1)).existsById(questionId);
        verify(questionRepository, times(1)).deleteById(questionId);
    }

    @Test
    public void testDeleteQuestionById_NotFound() {
        // Arrange
        Long questionId = 1L;

        when(questionRepository.existsById(questionId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> questionService.deleteQuestionById(questionId));

        assertEquals("Question with id: " + questionId + " not found", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).existsById(questionId);
        verify(questionRepository, never()).deleteById(questionId);
    }

    @Test
    public void testCreateLike() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        Question question = new Question();
        question.setId(questionId);

        User user = new User();
        user.setId(userId);

        QuestionLike newLike = new QuestionLike();
        newLike.setQuestion(question);
        newLike.setUser(user);
        newLike.setLikeDate(LocalDate.now());

        QuestionLikeResponseDTO responseDTO = new QuestionLikeResponseDTO();
        responseDTO.setQuestionId(questionId);
        responseDTO.setUserId(userId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionLikeRepository.existsByQuestionAndUser(question, user)).thenReturn(false);
        when(questionLikeRepository.save(any(QuestionLike.class))).thenReturn(newLike);
        when(questionLikeMapper.convertToResponseDTO(newLike)).thenReturn(responseDTO);

        // Act
        QuestionLikeResponseDTO result = questionService.createLike(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(questionRepository, times(1)).findById(questionId);
        verify(userRepository, times(1)).findById(userId);
        verify(questionLikeRepository, times(1)).existsByQuestionAndUser(question, user);
        verify(questionLikeRepository, times(1)).save(any(QuestionLike.class));
        verify(questionLikeMapper, times(1)).convertToResponseDTO(newLike);
    }

    @Test
    public void testCreateLike_QuestionNotFound() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> questionService.createLike(requestDTO));

        assertEquals("Question not found with id: " + questionId, exception.getMessage());
        verify(questionRepository, times(1)).findById(questionId);
        verify(userRepository, never()).findById(userId);
        verify(questionLikeRepository, never()).existsByQuestionAndUser(any(Question.class), any(User.class));
        verify(questionLikeRepository, never()).save(any(QuestionLike.class));
    }


    @Test
    public void testCreateLike_UserAlreadyLiked() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        Question question = new Question();
        question.setId(questionId);

        User user = new User();
        user.setId(userId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionLikeRepository.existsByQuestionAndUser(question, user)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> questionService.createLike(requestDTO));

        assertEquals("User already liked this question", exception.getMessage());
        verify(questionRepository, times(1)).findById(questionId);
        verify(userRepository, times(1)).findById(userId);
        verify(questionLikeRepository, times(1)).existsByQuestionAndUser(question, user);
        verify(questionLikeRepository, never()).save(any(QuestionLike.class));
    }

    @Test
    public void testDeleteLike() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        Question question = new Question();
        question.setId(questionId);

        User user = new User();
        user.setId(userId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userService.getEntityById(userId)).thenReturn(user);
        when(questionLikeRepository.existsByQuestionAndUser(question, user)).thenReturn(true);

        // Act
        questionService.deleteLike(requestDTO);

        // Assert
        verify(questionRepository, times(1)).findById(questionId);
        verify(userService, times(1)).getEntityById(userId);
        verify(questionLikeRepository, times(1)).existsByQuestionAndUser(question, user);
        verify(questionLikeRepository, times(1)).deleteByQuestionAndUser(question, user);
    }

    @Test
    public void testDeleteLike_QuestionNotFound() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> questionService.deleteLike(requestDTO));

        assertEquals("Article not found with id: " + questionId, exception.getMessage());
        verify(questionRepository, times(1)).findById(questionId);
        verify(userService, never()).getEntityById(userId);
        verify(questionLikeRepository, never()).existsByQuestionAndUser(any(Question.class), any(User.class));
        verify(questionLikeRepository, never()).deleteByQuestionAndUser(any(Question.class), any(User.class));
    }


    @Test
    public void testDeleteLike_UserNotFound() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        Question question = new Question();
        question.setId(questionId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userService.getEntityById(userId)).thenThrow(new ResourceNotFoundException("User not found with id: " + userId));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> questionService.deleteLike(requestDTO));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(questionRepository, times(1)).findById(questionId);
        verify(userService, times(1)).getEntityById(userId);
        verify(questionLikeRepository, never()).existsByQuestionAndUser(any(Question.class), any(User.class));
        verify(questionLikeRepository, never()).deleteByQuestionAndUser(any(Question.class), any(User.class));
    }

    @Test
    public void testDeleteLike_UserDidNotLike() {
        // Arrange
        Long questionId = 1L;
        Long userId = 1L;

        QuestionLikeRequestDTO requestDTO = new QuestionLikeRequestDTO();
        requestDTO.setQuestionId(questionId);
        requestDTO.setUserId(userId);

        Question question = new Question();
        question.setId(questionId);

        User user = new User();
        user.setId(userId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userService.getEntityById(userId)).thenReturn(user);
        when(questionLikeRepository.existsByQuestionAndUser(question, user)).thenReturn(false);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> questionService.deleteLike(requestDTO));

        assertEquals("User did not like this article", exception.getMessage());
        verify(questionRepository, times(1)).findById(questionId);
        verify(userService, times(1)).getEntityById(userId);
        verify(questionLikeRepository, times(1)).existsByQuestionAndUser(question, user);
        verify(questionLikeRepository, never()).deleteByQuestionAndUser(any(Question.class), any(User.class));
    }

    @Test
    public void testGetFilteredList_likes(){
        // Arrange
        List<String> types = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            question.setTitle("Title" + i);
            question.setContent("Content" + i);
            question.setType("type 1");
            question.setPublicationDate(LocalDate.now());
            questions.add(question);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Question> page = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findLikedQuestions(pageable)).thenReturn(page);
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(0L);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(0L);
        when(questionMapper.convertToDTO(any(Question.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new QuestionResponseDTO(question.getId(), question.getTitle(), question.getContent(), question.getPublicationDate().toString(), question.getType(), question.getId(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setOrderBy("likes");
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<QuestionResponseDTO> result = questionService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        for (int i = 0; i < 5; i++) {
            QuestionResponseDTO dto = result.getContent().get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
            assertEquals("Content" + i, dto.getContent());
        }

        // Verify
        verify(questionRepository, times(1)).findLikedQuestions(pageable);
        verify(questionLikeRepository, times(5)).countByQuestion(any(Question.class));
        verify(questionRepository, times(5)).countAnswersByQuestion(any(Question.class));
        verify(questionMapper, times(5)).convertToDTO(any(Question.class), anyLong(), anyLong());

    }

    @Test
    public void testGetFilteredList_answers(){
        // Arrange
        List<String> types = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            question.setTitle("Title" + i);
            question.setContent("Content" + i);
            question.setType("type 1");
            question.setPublicationDate(LocalDate.now());
            questions.add(question);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Question> page = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findAnsweredQuestions(pageable)).thenReturn(page);
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(0L);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(0L);
        when(questionMapper.convertToDTO(any(Question.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new QuestionResponseDTO(question.getId(), question.getTitle(), question.getContent(), question.getPublicationDate().toString(), question.getType(), question.getId(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setOrderBy("answers");
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<QuestionResponseDTO> result = questionService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        for (int i = 0; i < 5; i++) {
            QuestionResponseDTO dto = result.getContent().get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
            assertEquals("Content" + i, dto.getContent());
        }

        // Verify
        verify(questionRepository, times(1)).findAnsweredQuestions(pageable);
        verify(questionLikeRepository, times(5)).countByQuestion(any(Question.class));
        verify(questionRepository, times(5)).countAnswersByQuestion(any(Question.class));
        verify(questionMapper, times(5)).convertToDTO(any(Question.class), anyLong(), anyLong());
    }

    @Test
    public void testGetFilteredList_recent(){
        // Arrange
        List<String> types = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            question.setTitle("Title" + i);
            question.setContent("Content" + i);
            question.setType("type 1");
            question.setPublicationDate(LocalDate.now());
            questions.add(question);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Question> page = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findRecentQuestions(pageable)).thenReturn(page);
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(0L);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(0L);
        when(questionMapper.convertToDTO(any(Question.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new QuestionResponseDTO(question.getId(), question.getTitle(), question.getContent(), question.getPublicationDate().toString(), question.getType(), question.getId(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<QuestionResponseDTO> result = questionService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        List<QuestionResponseDTO> content = result.getContent();
        for (int i = 0; i < 5; i++) {
            QuestionResponseDTO dto = content.get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
            assertEquals("Content" + i, dto.getContent());
        }
    }

    @Test
    public void testGetFilteredList_likesTyped(){
        // Arrange
        List<String> types = new ArrayList<>();
        types.add("type 1");
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            question.setTitle("Title" + i);
            question.setContent("Content" + i);
            question.setType("type 1");
            question.setPublicationDate(LocalDate.now());
            questions.add(question);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Question> page = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findLikedQuestionByType(types, pageable)).thenReturn(page);
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(0L);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(0L);
        when(questionMapper.convertToDTO(any(Question.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new QuestionResponseDTO(question.getId(), question.getTitle(), question.getContent(), question.getPublicationDate().toString(), question.getType(), question.getId(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);
        filterRequestDTO.setOrderBy("likes");

        // Act
        Page<QuestionResponseDTO> result = questionService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        List<QuestionResponseDTO> content = result.getContent();
        for (int i = 0; i < 5; i++) {
            QuestionResponseDTO dto = content.get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
            assertEquals("Content" + i, dto.getContent());
        }
    }

    @Test
    public void testGetFilteredList_answersTyped(){
        // Arrange
        List<String> types = new ArrayList<>();
        types.add("type 1");
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            question.setTitle("Title" + i);
            question.setContent("Content" + i);
            question.setType("type 1");
            question.setPublicationDate(LocalDate.now());
            questions.add(question);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Question> page = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findAnsweredQuestionByType(types, pageable)).thenReturn(page);
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(0L);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(0L);
        when(questionMapper.convertToDTO(any(Question.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new QuestionResponseDTO(question.getId(), question.getTitle(), question.getContent(), question.getPublicationDate().toString(), question.getType(), question.getId(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);
        filterRequestDTO.setOrderBy("answers");

        // Act
        Page<QuestionResponseDTO> result = questionService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        List<QuestionResponseDTO> content = result.getContent();
        for (int i = 0; i < 5; i++) {
            QuestionResponseDTO dto = content.get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
            assertEquals("Content" + i, dto.getContent());
        }
    }

    @Test
    public void testGetFilteredList_recentTyped(){
        // Arrange
        List<String> types = new ArrayList<>();
        types.add("type 1");
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            question.setTitle("Title" + i);
            question.setContent("Content" + i);
            question.setType("type 1");
            question.setPublicationDate(LocalDate.now());
            questions.add(question);
        }

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Question> page = new PageImpl<>(questions, pageable, questions.size());

        when(questionRepository.findRecentQuestionByType(types, pageable)).thenReturn(page);
        when(questionLikeRepository.countByQuestion(any(Question.class))).thenReturn(0L);
        when(questionRepository.countAnswersByQuestion(any(Question.class))).thenReturn(0L);
        when(questionMapper.convertToDTO(any(Question.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Long likes = 0L;
            Long answers = 0L;
            return new QuestionResponseDTO(question.getId(), question.getTitle(), question.getContent(), question.getPublicationDate().toString(), question.getType(), question.getId(), likes, answers);
        });

        FilterRequestDTO filterRequestDTO = new FilterRequestDTO();
        filterRequestDTO.setPageSize(5);
        filterRequestDTO.setPageNum(0);
        filterRequestDTO.setCategories(types);

        // Act
        Page<QuestionResponseDTO> result = questionService.getFilteredList(filterRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        List<QuestionResponseDTO> content = result.getContent();
        for (int i = 0; i < 5; i++) {
            QuestionResponseDTO dto = content.get(i);
            assertEquals(i, dto.getId());
            assertEquals("Title" + i, dto.getTitle());
            assertEquals("Content" + i, dto.getContent());
        }
    }
}
