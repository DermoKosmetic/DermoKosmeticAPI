package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.AnswerLikeRequestDTO;
import com.dk.dermokometicapi.model.entity.AnswerLike;
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
}
