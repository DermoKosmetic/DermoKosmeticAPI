package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.dto.QuestionRequestDTO;
import com.dk.dermokometicapi.model.dto.QuestionResponseDTO;
import com.dk.dermokometicapi.model.dto.UserRequestDTO;
import com.dk.dermokometicapi.model.dto.UserResponseDTO;
import com.dk.dermokometicapi.model.entity.Question;
import com.dk.dermokometicapi.model.entity.User;
import com.dk.dermokometicapi.model.exception.BadRequestException;
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

}
