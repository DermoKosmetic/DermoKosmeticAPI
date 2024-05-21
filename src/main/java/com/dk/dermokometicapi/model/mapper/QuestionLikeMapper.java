package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.QuestionLikeRequestDTO;
import com.dk.dermokometicapi.model.dto.QuestionLikeResponseDTO;
import com.dk.dermokometicapi.model.entity.QuestionLike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
