package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Article;
import com.dk.dermokometicapi.models.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // CRUD operations
    Optional<Comment> findById (long id);
    boolean existsById (long id);
    void deleteById (long id);
    List<Comment> getByArticle_Id(Long articleId);
    long countByParentComment(Comment parentComment);

    // Find comments by article id ordered by publication date
    @Query("SELECT c FROM Comment c WHERE c.article.id = :article_id AND c.parentComment IS NULL ORDER BY c.publicationDate DESC")
    Page<Comment> findRecentCommentsByArticle_id(Long article_id, Pageable pageable);

    // Find comments by article id ordered by like number
    @Query(value = "SELECT c.* FROM comments c LEFT JOIN comment_likes cl ON c.id = cl.comment_id WHERE c.article_id = :article_id AND c.parent_id IS NULL GROUP BY c.id ORDER BY COUNT(cl.id) DESC", nativeQuery = true)
    Page<Comment> findLikedCommentsByArticle_id(Long article_id, Pageable pageable);

    // Find comments by article id ordered by response number
    @Query(value = "SELECT c.* FROM comments c LEFT JOIN comments c2 ON c.id = c2.parent_id WHERE c.article_id = :article_id AND c.parent_id IS NULL GROUP BY c.id ORDER BY COUNT(c2.id) DESC", nativeQuery = true)
    Page<Comment> findCommentedCommentsByArticle_id(Long article_id, Pageable pageable);

    // Find comments by parent comment id ordered by publication date
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parent_comment_id ORDER BY c.publicationDate DESC")
    Page<Comment> findRecentCommentsByParentComment_id(Long parent_comment_id, Pageable pageable);

    // Find comments by parent comment id ordered by like number
    @Query(value = "SELECT c.* FROM comments c LEFT JOIN comment_likes cl ON c.id = cl.comment_id WHERE c.parent_id = :parent_comment_id GROUP BY c.id ORDER BY COUNT(cl.id) DESC", nativeQuery = true)
    Page<Comment> findLikedCommentsByParentComment_id(Long parent_comment_id, Pageable pageable);

    // Find comments by parent comment id ordered by response number
    @Query(value = "SELECT c.* FROM comments c LEFT JOIN comments c2 ON c.id = c2.parent_id WHERE c.parent_id = :parent_comment_id GROUP BY c.id ORDER BY COUNT(c2.id) DESC", nativeQuery = true)
    Page<Comment> findCommentedCommentsByParentComment_id(Long parent_comment_id, Pageable pageable);

    Long countByArticle(Article article);

    void deleteByArticle(Article article);

    /*
    *     @Query("SELECT a FROM Article a ORDER BY a.publicationDate DESC")
    Page<Article> findRecentArticles(Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN article_likes al ON a.id = al.article_id GROUP BY a.id ORDER BY COUNT(al.id) DESC", nativeQuery = true)
    Page<Article> findLikedArticles(Pageable pageable);

    @Query(value = "SELECT a.* FROM articles a LEFT JOIN comments c ON a.id = c.article_id GROUP BY a.id ORDER BY COUNT(c.id) DESC", nativeQuery = true)
    Page<Article> findCommentedArticles(Pageable pageable);
    * */

}
