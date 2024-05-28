package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Article;
import com.dk.dermokometicapi.models.entities.ArticleLike;
import com.dk.dermokometicapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    boolean existsByArticleAndUser(Article article, User user);
    void deleteByArticleAndUser(Article article, User user);

    Long countByArticle(Article article);

    void deleteByArticle(Article article);
}
