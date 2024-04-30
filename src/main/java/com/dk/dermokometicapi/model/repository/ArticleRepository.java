package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ArticleRepository extends JpaRepository<Article, Long>{

    // Regular CRUD operations

    Optional<Article> findByTitle(String title);

    void deleteByTitle(String title);

    List<Article> findByTitleContaining(String title);

    boolean existsByTitle(String title);

    // Find likes and comments of an article

    @Query("SELECT COUNT(al) FROM Article a INNER JOIN a.likes al WHERE a.id = :id")
    Long findArticleLikesById(@Param("id") Long id);

    @Query("SELECT COUNT(ac) FROM Article a INNER JOIN a.comments ac WHERE a.id = :id")
    Long findArticleCommentsById(@Param("id") Long id);

    // Filtering and ordering articles

    @Query("SELECT a FROM Article a ORDER BY a.publicationDate DESC")
    List<Article> findRecentArticles(Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN a.likes al GROUP BY a.id ORDER BY COUNT(al) DESC")
    List<Article> findLikedArticles(Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN a.comments c GROUP BY a.id ORDER BY COUNT(c) DESC")
    List<Article> findCommentedArticles(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.type IN :types")
    List<Article> findByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.type IN :types ORDER BY a.publicationDate DESC")
    List<Article> findRecentArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN a.likes al WHERE a.type IN :types GROUP BY a.id ORDER BY COUNT(al) DESC")
    List<Article> findLikedArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN a.comments c WHERE a.type IN :types GROUP BY a.id ORDER BY COUNT(c) DESC")
    List<Article> findCommentedArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT w.id FROM Article a LEFT JOIN a.writers w WHERE a.id = :articleId")
    List<Long> findWritersByArticleId(Long articleId);
}
