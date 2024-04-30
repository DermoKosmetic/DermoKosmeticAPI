package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.AnswerRequestDTO;
import com.dk.dermokometicapi.model.dto.AnswerResponseDTO;
import com.dk.dermokometicapi.model.entity.Answer;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnswerMapper {

    private final ModelMapper modelMapper;

    public Answer convertToEntity(AnswerRequestDTO answerRequestDTO) {
        return modelMapper.map(answerRequestDTO, Answer.class);
    }

    public AnswerResponseDTO convertToDTO(Answer answer, Long likes, Long responses) {
        AnswerResponseDTO answerResponseDTO = modelMapper.map(answer, AnswerResponseDTO.class);
        answerResponseDTO.setLikes(likes);
        answerResponseDTO.setResponses(responses);
        return answerResponseDTO;
    }
}
