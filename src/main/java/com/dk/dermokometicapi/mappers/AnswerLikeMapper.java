package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.AnswerLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.AnswerLikeResponseDTO;
import com.dk.dermokometicapi.models.entities.AnswerLike;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnswerLikeMapper {
    private final ModelMapper modelMapper;

    public AnswerLike convertToEntity(AnswerLikeRequestDTO answerLikeRequestDTO) {
        return modelMapper.map(answerLikeRequestDTO, AnswerLike.class);
    }

    public AnswerLikeResponseDTO convertToDTO(AnswerLike answerLike) {
        return modelMapper.map(answerLike, AnswerLikeResponseDTO.class);
    }
}
