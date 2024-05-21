package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.entity.Question;
import com.dk.dermokometicapi.model.entity.QuestionLike;
import com.dk.dermokometicapi.model.entity.User;
import com.dk.dermokometicapi.model.exception.BadRequestException;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.ArticleMapper;
import com.dk.dermokometicapi.model.mapper.QuestionLikeMapper;
import com.dk.dermokometicapi.model.mapper.QuestionMapper;
import com.dk.dermokometicapi.model.mapper.UserMapper;
import com.dk.dermokometicapi.model.repository.ArticleRepository;
import com.dk.dermokometicapi.model.repository.QuestionLikeRepository;
import com.dk.dermokometicapi.model.repository.QuestionRepository;
import com.dk.dermokometicapi.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final QuestionLikeRepository questionLikeRepository;
    private final UserService userService;
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;
    private final QuestionLikeMapper questionLikeMapper;


    //create question
    public QuestionResponseDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        if (questionRepository.existsByTitle(questionRequestDTO.getTitle())) {
            throw new BadRequestException("Question with title: " + questionRequestDTO.getTitle() + " already exists");
        }
        Question newQuestion = questionMapper.convertToEntity(questionRequestDTO);
        newQuestion.setPublicationDate(LocalDate.now());
        questionRepository.save(newQuestion);
        return questionMapper.convertToDTO(newQuestion, 0L, 0L);
    }

    private List<QuestionResponseDTO> getQuestionListDTO(List<Question> questions) {
        return questions.stream()
                .map(question -> {
                    Long likes = questionRepository.findQuestionLikesById(question.getId());
                    Long answers = questionRepository.findQuestionAnswersById(question.getId());
                    System.out.println(likes +  answers);
                    return questionMapper.convertToDTO(question, likes, answers);
                })
                .collect(Collectors.toList());
    }

    //get all articles
    public List<QuestionResponseDTO> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return getQuestionListDTO(questions);
    }

    public QuestionResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question with id: " + id + " not found"));
        Long likes = questionRepository.findQuestionLikesById(id);
        Long answers = questionRepository.findQuestionAnswersById(id);
        return questionMapper.convertToDTO(question, likes, answers);
    }

    public QuestionResponseDTO getQuestionByTitle(String title) {
        Question question = questionRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Question with title: " + title + " not found"));
        Long likes = questionRepository.findQuestionLikesById(question.getId());
        Long answers = questionRepository.findQuestionAnswersById(question.getId());
        return questionMapper.convertToDTO(question, likes, answers);
    }

    // delete by id
    public void deleteQuestionById(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question with id: " + id + " not found");
        }
        questionRepository.deleteById(id);
    }

    // like
    public QuestionLikeResponseDTO createLike(QuestionLikeRequestDTO questionLikeRequestDTO) {
        Long questionId = questionLikeRequestDTO.getQuestionId();
        Long userId = questionLikeRequestDTO.getUserId();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (questionLikeRepository.existsByQuestionAndUser(question, user)) {
            throw new BadRequestException("User already liked this question");
        }

        QuestionLike newLike = new QuestionLike();
        newLike.setQuestion(question);
        newLike.setUser(user);
        newLike.setLikeDate(LocalDate.now());
        questionLikeRepository.save(newLike);

        return questionLikeMapper.convertToResponseDTO(newLike);
    }

    //dislike
    public void deleteLike(QuestionLikeRequestDTO questionLikeRequestDTO) {
        Long questionId = questionLikeRequestDTO.getQuestionId();
        Long userId = questionLikeRequestDTO.getUserId();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + questionId));
        User user = userService.getEntityById(userId);
        if (!questionLikeRepository.existsByQuestionAndUser(question, user)) {
            throw new BadRequestException("User did not like this article");
        }
        questionLikeRepository.deleteByQuestionAndUser(question, user);
    }

}
