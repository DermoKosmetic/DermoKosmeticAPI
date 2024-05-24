package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.ArticleRequestDTO;
import com.dk.dermokometicapi.models.dto.ArticleResponseDTO;
import com.dk.dermokometicapi.models.dto.ArticleSummaryResponseDTO;
import com.dk.dermokometicapi.models.entity.Article;
import com.dk.dermokometicapi.models.entity.ArticleDetail;
import com.dk.dermokometicapi.models.entity.Writer;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ArticleMapper {

    private final ModelMapper modelMapper;

    public Article convertToEntity(ArticleRequestDTO articleRequestDTO, List<Writer> writers) {
        Article article = modelMapper.map(articleRequestDTO, Article.class);
        article.setWriters(writers);
        return article;
    }

    public ArticleDetail convertToDetailEntity(ArticleRequestDTO articleRequestDTO, Article article) {
        return modelMapper.map(articleRequestDTO, ArticleDetail.class);
    }

    public ArticleSummaryResponseDTO convertToSummaryDTO(Article article, Long likes, Long comments) {
        ArticleSummaryResponseDTO articleSummaryResponseDTO = modelMapper.map(article, ArticleSummaryResponseDTO.class);
        articleSummaryResponseDTO.setLikes(likes);
        articleSummaryResponseDTO.setComments(comments);
        return articleSummaryResponseDTO;
    }

    public ArticleResponseDTO convertToDTO(Article article, ArticleDetail articleDetail, Long likes, Long comments, List<Long> writerIds) {
        ArticleResponseDTO articleResponseDTO = modelMapper.map(article, ArticleResponseDTO.class);
        modelMapper.map(articleDetail, articleResponseDTO);
        articleResponseDTO.setLikes(likes);
        articleResponseDTO.setComments(comments);
        articleResponseDTO.setWriterIds(writerIds);
        return articleResponseDTO;
    }
}
