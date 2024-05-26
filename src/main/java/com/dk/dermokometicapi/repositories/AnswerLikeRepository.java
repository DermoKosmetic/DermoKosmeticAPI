package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Answer;
import com.dk.dermokometicapi.models.entities.AnswerLike;
import com.dk.dermokometicapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {
    boolean existsByAnswerAndUser(Answer answer, User user);
    void deleteByAnswerAndUser(Answer answer, User user);
    long countByAnswer(Answer answer);
}
