package com.dk.dermokometicapi.model.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_likes")
public class QuestionLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate likeDate;

    @ManyToOne
    @JoinColumn(
            name = "question_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Question question;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false
    )
    private User user;
}
