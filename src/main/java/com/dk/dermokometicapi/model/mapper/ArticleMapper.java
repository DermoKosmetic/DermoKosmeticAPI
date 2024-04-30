package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.ArticleRequestDTO;
import com.dk.dermokometicapi.model.dto.ArticleResponseDTO;
import com.dk.dermokometicapi.model.entity.Article;
import com.dk.dermokometicapi.model.entity.ArticleDetail;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ArticleMapper {

    private final ModelMapper modelMapper;

    public Article convertToEntity(ArticleRequestDTO articleRequestDTO) {
        return modelMapper.map(articleRequestDTO, Article.class);
    }

    public ArticleDetail convertToEntity(ArticleRequestDTO articleRequestDTO, Article article) {
        return modelMapper.map(articleRequestDTO, ArticleDetail.class);
    }

    public ArticleResponseDTO convertToDTO(Article article, ArticleDetail articleDetail, Long likes, Long comments, List<Long> writers) {
        ArticleResponseDTO articleResponseDTO = modelMapper.map(article, ArticleResponseDTO.class);
        modelMapper.map(articleDetail, articleResponseDTO);
        articleResponseDTO.setLikes(likes);
        articleResponseDTO.setComments(comments);
        articleResponseDTO.setWriters(writers);
        return articleResponseDTO;
    }
}
