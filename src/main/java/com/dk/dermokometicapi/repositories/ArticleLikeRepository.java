package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entity.Article;
import com.dk.dermokometicapi.models.entity.ArticleLike;
import com.dk.dermokometicapi.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    boolean existsByArticleAndUser(Article article, User user);
    void deleteByArticleAndUser(Article article, User user);
}
