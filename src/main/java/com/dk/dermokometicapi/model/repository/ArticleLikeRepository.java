package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Article;
import com.dk.dermokometicapi.model.entity.ArticleLike;
import com.dk.dermokometicapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    boolean existsByArticleAndUser(Article article, User user);
    void deleteByArticleAndUser(Article article, User user);
}
