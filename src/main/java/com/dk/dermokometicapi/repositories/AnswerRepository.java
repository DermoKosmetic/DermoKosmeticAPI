package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entities.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // CRUD operations
    Optional<Answer> findById (long id);
    boolean existsById (long id);
    void deleteById (long id);
    List<Answer> getByQuestion_Id(Long articleId);
    long countByParentAnswer(Answer parentAnswer);

    // Find comments by question id ordered by publication date
    @Query("SELECT a FROM Answer a WHERE a.question.id = :question_id AND a.parentAnswer IS NULL ORDER BY a.publicationDate DESC")
    Page<Answer> findRecentByQuestionId(Long question_id, Pageable pageable);

    // Find comments by question id ordered by like number
    @Query(value = "SELECT a.* FROM answers a LEFT JOIN answer_likes al ON a.id = al.answer_id WHERE a.question_id = :question_id AND a.parent_id IS NULL GROUP BY a.id ORDER BY COUNT(al.id) DESC", nativeQuery = true)
    Page<Answer> findLikedByQuestionId(Long question_id, Pageable pageable);

    // Find comments by question id ordered by response number
    @Query(value = "SELECT a.* FROM answers a LEFT JOIN answers a2 ON a.id = a2.parent_id WHERE a.question_id = :question_id AND a.parent_id IS NULL GROUP BY a.id ORDER BY COUNT(a2.id) DESC", nativeQuery = true)
    Page<Answer> findAnsweredByQuestionId(Long question_id, Pageable pageable);

    // Find answers by parent answer id ordered by publication date
    @Query("SELECT a FROM Answer a WHERE a.parentAnswer.id = :parent_answer_id ORDER BY a.publicationDate DESC")
    Page<Answer> findRecentByParentCommentId(Long parent_answer_id, Pageable pageable);

    // Find answers by parent answer id ordered by like number
    @Query(value = "SELECT a.* FROM answers a LEFT JOIN answer_likes al ON a.id = al.answer_id WHERE a.parent_id = :parent_answer_id GROUP BY a.id ORDER BY COUNT(al.id) DESC", nativeQuery = true)
    Page<Answer> findLikedByParentAnswerId(Long parent_answer_id, Pageable pageable);

    // Find answers by parent answer id ordered by response number
    @Query(value = "SELECT a.* FROM answers a LEFT JOIN answers a2 ON a.id = a2.parent_id WHERE a.parent_id = :parent_answer_id GROUP BY a.id ORDER BY COUNT(a2.id) DESC", nativeQuery = true)
    Page<Answer> findCommentedByParentAnswerId(Long parent_answer_id, Pageable pageable);
}
