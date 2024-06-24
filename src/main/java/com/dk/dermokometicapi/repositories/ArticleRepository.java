package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ArticleRepository extends JpaRepository<Article, Long>{

    // Regular CRUD operations

    @Query("SELECT a FROM Article a")
    List<Article> getAll();

    Optional<Article> findByTitle(String title);

    void deleteByTitle(String title);

    List<Article> findByTitleContaining(String title);

    boolean existsByTitle(String title);

    // Filtering and ordering articles

    @Query("SELECT a FROM Article a WHERE a.type IN :types ORDER BY a.publicationDate DESC")
    Page<Article> findRecentArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN article_likes al ON a.id = al.article_id WHERE a.type IN :types GROUP BY a.id ORDER BY COUNT(al.id) DESC", nativeQuery = true)
    Page<Article> findLikedArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN comments c ON a.id = c.article_id WHERE a.type IN :types GROUP BY a.id ORDER BY COUNT(c.id) DESC", nativeQuery = true)
    Page<Article> findCommentedArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT DISTINCT a.type FROM Article a")
    List<String> findDistinctTypes();
}
