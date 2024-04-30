package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Article;
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

    // Find likes and comments of an article

    @Query(value = "SELECT COUNT(*) FROM articles a LEFT JOIN article_like al ON a.id = al.article_id WHERE al.article_id = :articleId", nativeQuery = true)
    Long findArticleLikesById(@Param("articleId") Long id);

    @Query(value = "SELECT COUNT(*) FROM articles a LEFT JOIN comments c ON a.id = c.article_id WHERE c.article_id = :articleId", nativeQuery = true)
    Long findArticleCommentsById(@Param("articleId") Long id);

    // Filtering and ordering articles

    @Query("SELECT a FROM Article a ORDER BY a.publicationDate DESC")
    Page<Article> findRecentArticles(Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN article_like al ON a.id = al.article_id GROUP BY a.id ORDER BY COUNT(al.id) DESC", nativeQuery = true)
    Page<Article> findLikedArticles(Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN comments c ON a.id = c.article_id GROUP BY a.id ORDER BY COUNT(c.id) DESC", nativeQuery = true)
    Page<Article> findCommentedArticles(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.type IN :types")
    Page<Article> findByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.type IN :types ORDER BY a.publicationDate DESC")
    Page<Article> findRecentArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN article_like al ON a.id = al.article_id WHERE a.type IN :types GROUP BY a.id ORDER BY COUNT(al.id) DESC", nativeQuery = true)
    Page<Article> findLikedArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN comments c ON a.id = c.article_id WHERE a.type IN :types GROUP BY a.id ORDER BY COUNT(c.id) DESC", nativeQuery = true)
    Page<Article> findCommentedArticleByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT w.id FROM Article a LEFT JOIN a.writers w WHERE a.id = :articleId")
    List<Long> findWritersByArticleId(Long articleId);
}
