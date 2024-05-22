package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q")
    List<Question> getAll();

    Optional<Question> findByTitle(String title);

    void deleteByTitle(String title);

    boolean existsByTitle(String title);

    List<Question> findByUserId(Long userId);

    List<Question> findByPublicationDate(LocalDate publicationDate);

    @Query(value = "SELECT COUNT(*) FROM questions q LEFT JOIN question_like ql ON q.id = ql.question_id WHERE ql.question_id = :questionId", nativeQuery = true)
    Long findQuestionLikesById(@Param("questionId") Long id);

    @Query(value = "SELECT COUNT(*) FROM questions q LEFT JOIN answers a ON q.id = a.question_id WHERE a.question_id = :questionId", nativeQuery = true)
    Long findQuestionAnswersById(@Param("questionId") Long id);

    @Query("SELECT COUNT(a) FROM Question q JOIN q.answers a WHERE q.id = :questionId")
    Long countAnswersByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT q FROM Question q ORDER BY q.publicationDate DESC")
    List<Question> findRecentQuestions();

    // Filtering and ordering questions

}
