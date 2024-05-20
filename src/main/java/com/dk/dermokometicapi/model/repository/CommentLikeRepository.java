package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Comment;
import com.dk.dermokometicapi.model.entity.CommentLike;
import com.dk.dermokometicapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    boolean existsById(long id);
    boolean existsByCommentAndUser(Comment comment, User user);
    void deleteByCommentAndUser (Comment comment, User user);
}
