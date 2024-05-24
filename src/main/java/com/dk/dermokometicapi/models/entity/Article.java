package com.dk.dermokometicapi.models.entity;
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
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = true, length = 1000)
    private String mainImg;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false)
    private LocalDate lastUpdateDate;

    @ManyToOne
    @JoinColumn(
            name = "detail_id",
            referencedColumnName = "id",
            nullable = false
    )
    private ArticleDetail articleDetail;

    @ManyToMany
    @JoinTable(
            name = "article_writers",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "writer_id")
    )
    private List<Writer> writers;
}
