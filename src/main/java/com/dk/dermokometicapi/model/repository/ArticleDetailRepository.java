package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.ArticleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleDetailRepository extends JpaRepository<ArticleDetail, Long>{
}
