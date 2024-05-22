package com.dk.dermokometicapi.model.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false
    )
    private User user;

    @OneToMany
    @JoinColumn(
            name = "answer_id",
            referencedColumnName = "id",
            nullable = true
    )
    private List<Answer> answers;

    @OneToMany
    @JoinColumn(
            name = "like_id",
            referencedColumnName = "id",
            nullable = true
    )
    private List<QuestionLike> likes;
}
