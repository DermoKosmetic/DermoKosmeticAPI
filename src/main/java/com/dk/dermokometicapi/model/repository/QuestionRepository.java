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

    // Filtering and ordering questions
    @Query("SELECT q FROM Question q ORDER BY q.publicationDate DESC")
    Page<Question> findRecentQuestions(Pageable pageable);

    @Query(value = "SELECT q.* FROM questions q LEFT JOIN question_like ql ON q.id = ql.question_id GROUP BY q.id ORDER BY COUNT(ql.id) DESC", nativeQuery = true)
    Page<Question> findLikedQuestions(Pageable pageable);

    @Query(value = "SELECT q.* FROM questions q LEFT JOIN answers a ON q.id = a.question_id GROUP BY q.id ORDER BY COUNT(a.id) DESC", nativeQuery = true)
    Page<Question> findAnsweredQuestions(Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.type IN :types")
    Page<Question> findByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.type IN :types ORDER BY q.publicationDate DESC")
    Page<Question> findRecentQuestionByType(@Param("types") List<String> types, Pageable pageable);

    @Query(value = "SELECT q.* FROM questions q LEFT JOIN question_like ql ON q.id = ql.question_id WHERE q.type IN :types GROUP BY q.id ORDER BY COUNT(ql.id) DESC", nativeQuery = true)
    Page<Question> findLikedQuestionByType(@Param("types") List<String> types, Pageable pageable);

    @Query(value = "SELECT q.* FROM questions q LEFT JOIN answers a ON q.id = a.question_id WHERE q.type IN :types GROUP BY q.id ORDER BY COUNT(a.id) DESC", nativeQuery = true)
    Page<Question> findAnsweredQuestionByType(@Param("types") List<String> types, Pageable pageable);

    @Query("SELECT u.id FROM Question q LEFT JOIN q.user u WHERE q.id = :questionId")
    List<Long> findUserByQuestionId(@Param("questionId") Long questionId);
}
