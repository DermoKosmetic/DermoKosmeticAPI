package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Question;
import com.dk.dermokometicapi.models.entities.QuestionLike;
import com.dk.dermokometicapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {
    boolean existsByQuestionAndUser(Question question, User user);
    void deleteByQuestionAndUser(Question question, User user);
}
