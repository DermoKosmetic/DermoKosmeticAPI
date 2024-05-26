package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.QuestionLikeRequestDTO;
import com.dk.dermokometicapi.models.dto.QuestionLikeResponseDTO;
import com.dk.dermokometicapi.models.entities.QuestionLike;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QuestionLikeMapper {
    private final ModelMapper modelMapper;

    public QuestionLike convertToEntity(QuestionLikeRequestDTO questionLikeRequestDTO) {
        return modelMapper.map(questionLikeRequestDTO, QuestionLike.class);
    }

    public QuestionLikeResponseDTO convertToResponseDTO(QuestionLike questionLike) {
        return modelMapper.map(questionLike, QuestionLikeResponseDTO.class);
    }
}
