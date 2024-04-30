package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.ArticleLikeRequestDTO;
import com.dk.dermokometicapi.model.dto.ArticleLikeResponseDTO;
import com.dk.dermokometicapi.model.entity.ArticleLike;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArticleLikeMapper {
    private final ModelMapper modelMapper;

    public ArticleLike convertToEntity(ArticleLikeRequestDTO articleLikeRequestDTO) {
        return modelMapper.map(articleLikeRequestDTO, ArticleLike.class);
    }

    public ArticleLikeResponseDTO convertToResponseDTO(ArticleLike articleLike) {
        return modelMapper.map(articleLike, ArticleLikeResponseDTO.class);
    }
}
