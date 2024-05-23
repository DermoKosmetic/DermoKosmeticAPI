package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Answer;
import com.dk.dermokometicapi.model.entity.AnswerLike;
import com.dk.dermokometicapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {
    boolean existsByAnswerAndUser(Answer answer, User user);
    void deleteByAnswerAndUser(Answer answer, User user);
    long countByAnswer(Answer answer);
}
