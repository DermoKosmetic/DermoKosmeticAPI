package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entity.Comment;
import com.dk.dermokometicapi.models.entity.CommentLike;
import com.dk.dermokometicapi.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    boolean existsById(long id);
    boolean existsByCommentAndUser(Comment comment, User user);
    void deleteByCommentAndUser (Comment comment, User user);
    long countByComment(Comment comment);
    Optional<CommentLike> findByComment_IdAndUser_Id(long commentId, long userId);
}
