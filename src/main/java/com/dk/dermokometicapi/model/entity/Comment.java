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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @ManyToOne
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            nullable = true
    )
    private Comment parentComment;

    @ManyToOne
    @JoinColumn(
            name = "article_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Article article;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false
    )
    private User user;

}
