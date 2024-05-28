package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Article;
import com.dk.dermokometicapi.models.entities.Comment;
import com.dk.dermokometicapi.models.entities.CommentLike;
import com.dk.dermokometicapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    boolean existsById(long id);
    boolean existsByCommentAndUser(Comment comment, User user);
    void deleteByCommentAndUser (Comment comment, User user);
    long countByComment(Comment comment);
    Optional<CommentLike> findByComment_IdAndUser_Id(long commentId, long userId);
    void deleteByComment_Article(Article article);
}
