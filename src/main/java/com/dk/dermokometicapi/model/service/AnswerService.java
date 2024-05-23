package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.entity.Answer;
import com.dk.dermokometicapi.model.entity.AnswerLike;
import com.dk.dermokometicapi.model.entity.Question;
import com.dk.dermokometicapi.model.entity.User;
import com.dk.dermokometicapi.model.exception.BadRequestException;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.AnswerLikeMapper;
import com.dk.dermokometicapi.model.mapper.AnswerMapper;
import com.dk.dermokometicapi.model.repository.AnswerLikeRepository;
import com.dk.dermokometicapi.model.repository.AnswerRepository;
import com.dk.dermokometicapi.model.repository.QuestionRepository;
import com.dk.dermokometicapi.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerMapper answerMapper;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerLikeMapper answerLikeMapper;

    public AnswerResponseDTO convertToDTO(Answer answer){
        long likes = answerLikeRepository.countByAnswer(answer);
        long answersCount = answerRepository.countByParentAnswer(answer);
        return answerMapper.convertToDTO(answer, likes, answersCount);
    }

    public List<AnswerResponseDTO> getAllAnswers(){
        return answerRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public AnswerResponseDTO getAnswerById(Long id){
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
        return convertToDTO(answer);
    }

    public AnswerResponseDTO addAnswer(AnswerRequestDTO answerRequestDTO){
        User user = userRepository.findById(answerRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + answerRequestDTO.getUserId()));

        Question question = questionRepository.findById(answerRequestDTO.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + answerRequestDTO.getQuestionId()));

        Answer parentAnswer = null;

        if(answerRequestDTO.getParentAnswerId() != null){
            parentAnswer = answerRepository.findById(answerRequestDTO.getParentAnswerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " ));
        }

        Answer answer = answerMapper.convertToEntity(answerRequestDTO, user, question, parentAnswer);
        answerRepository.save(answer);
        return convertToDTO(answer);
    }

    // get answers by question id
    public Page<AnswerResponseDTO> getAnswersByQuestionId(Long questionId, ListRequestDTO listRequestDTO){
        Pageable pageable = Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum());
        return switch (listRequestDTO.getOrderBy()) {
            case "likes" -> answerRepository.findLikedByQuestionId(questionId, pageable).map(this::convertToDTO);
            case "responses" -> answerRepository.findAnsweredByQuestionId(questionId, pageable).map(this::convertToDTO);
            default -> answerRepository.findRecentByQuestionId(questionId, pageable).map(this::convertToDTO);
        };
    }

    // get answers by parent answer id
    public Page<AnswerResponseDTO> getAnswersByParentId(Long parentAnswerId, ListRequestDTO listRequestDTO){
        Pageable pageable = Pageable.ofSize(listRequestDTO.getPageSize()).withPage(listRequestDTO.getPageNum());
        return switch (listRequestDTO.getOrderBy()) {
            case "likes" -> answerRepository.findLikedByParentAnswerId(parentAnswerId, pageable).map(this::convertToDTO);
            case "responses" -> answerRepository.findCommentedByParentAnswerId(parentAnswerId, pageable).map(this::convertToDTO);
            default -> answerRepository.findRecentByParentCommentId(parentAnswerId, pageable).map(this::convertToDTO);
        };
    }

    public void deleteAnswer(Long id){
        if(!answerRepository.existsById(id)){
            throw new ResourceNotFoundException("Answer not found with id: " + id);
        }
        answerRepository.deleteById(id);
    }

    // like answer
    public AnswerLikeResponseDTO likeAnswer(AnswerLikeRequestDTO answerLikeRequestDTO){
        Long answerId = answerLikeRequestDTO.getAnswerId();
        Long userId = answerLikeRequestDTO.getUserId();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if(answerLikeRepository.existsByAnswerAndUser(answer, user)){
            throw new BadRequestException("User already liked this answer");
        }
        AnswerLike answerLike = new AnswerLike();
        answerLike.setAnswer(answer);
        answerLike.setUser(user);
        answerLike.setLikeDate(LocalDate.now());
        answerLikeRepository.save(answerLike);
        return answerLikeMapper.convertToDTO(answerLike);
    }

    // unlike answer
    @Transactional
    public void unlikeAnswer(AnswerLikeRequestDTO answerLikeRequestDTO){
        Long answerId = answerLikeRequestDTO.getAnswerId();
        Long userId = answerLikeRequestDTO.getUserId();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if(!answerLikeRepository.existsByAnswerAndUser(answer, user)){
            throw new BadRequestException("User did not like this answer");
        }
        answerLikeRepository.deleteByAnswerAndUser(answer, user);
    }
}
