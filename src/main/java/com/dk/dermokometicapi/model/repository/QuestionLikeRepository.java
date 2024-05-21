package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Question;
import com.dk.dermokometicapi.model.entity.QuestionLike;
import com.dk.dermokometicapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {

    boolean existsByQuestionAndUser(Question question, User user);
    void deleteByQuestionAndUser(Question question, User user);
}
