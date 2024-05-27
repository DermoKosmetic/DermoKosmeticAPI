package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.mappers.QuestionLikeMapper;
import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.Question;
import com.dk.dermokometicapi.models.entities.QuestionLike;
import com.dk.dermokometicapi.models.entities.User;

import com.dk.dermokometicapi.mappers.ArticleMapper;
import com.dk.dermokometicapi.mappers.QuestionMapper;
import com.dk.dermokometicapi.repositories.ArticleRepository;
import com.dk.dermokometicapi.repositories.QuestionLikeRepository;
import com.dk.dermokometicapi.repositories.QuestionRepository;
import com.dk.dermokometicapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;
    private final QuestionLikeMapper questionLikeMapper;


    private QuestionResponseDTO getDTO(Question question){
        Long likes = questionLikeRepository.countByQuestion(question);
        Long answers = questionRepository.countAnswersByQuestion(question);
        return questionMapper.convertToDTO(question, likes, answers);
    }


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

    //get all articles
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().stream().map(this::getDTO).toList();
    }

    public QuestionResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question with id: " + id + " not found"));
        return getDTO(question);
    }

    public QuestionResponseDTO getQuestionByTitle(String title) {
        Question question = questionRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Question with title: " + title + " not found"));
        return getDTO(question);
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

    //filters



    public Page<QuestionResponseDTO> getFilteredList(FilterRequestDTO filterRequestDTO){
        if(filterRequestDTO.getOrderBy() == null) filterRequestDTO.setOrderBy("recent");
        Pageable pageable = PageRequest.of(filterRequestDTO.getPageNum(), filterRequestDTO.getPageSize());
        List<String> types = filterRequestDTO.getCategories();
        if(filterRequestDTO.getCategories().isEmpty()){
            return switch (filterRequestDTO.getOrderBy()) {
                case "likes" -> questionRepository.findLikedQuestions(pageable)
                        .map(this::getDTO);
                case "answers" -> questionRepository.findAnsweredQuestions(pageable)
                                .map(this::getDTO);
                default -> questionRepository.findRecentQuestions(pageable)
                        .map(this::getDTO);
            };
        }else{
            return switch (filterRequestDTO.getOrderBy()) {
                case "likes" -> questionRepository.findLikedQuestionByType(types, pageable)
                        .map(this::getDTO);
                case "answers" -> questionRepository.findAnsweredQuestionByType(types, pageable)
                        .map(this::getDTO);
                default -> questionRepository.findRecentQuestionByType(types, pageable)
                        .map(this::getDTO);
            };
        }
    }


}
