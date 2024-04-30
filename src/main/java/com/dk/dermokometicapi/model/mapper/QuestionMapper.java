package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.QuestionRequestDTO;
import com.dk.dermokometicapi.model.dto.QuestionResponseDTO;
import com.dk.dermokometicapi.model.entity.Question;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QuestionMapper {
    private final ModelMapper modelMapper;

    public Question convertToEntity(QuestionRequestDTO questionRequestDTO) {
        return modelMapper.map(questionRequestDTO, Question.class);
    }

    public QuestionResponseDTO convertToDTO(Question question, Long likes, Long answers) {
        QuestionResponseDTO questionResponseDTO = modelMapper.map(question, QuestionResponseDTO.class);
        questionResponseDTO.setLikes(likes);
        questionResponseDTO.setAnswers(answers);
        return questionResponseDTO;
    }
}
