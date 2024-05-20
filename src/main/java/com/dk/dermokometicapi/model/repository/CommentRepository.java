package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Comment;
import com.dk.dermokometicapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // operaciones crud y +
    Optional<Comment> findById (long id);
    boolean existsById (long id);
    void deleteById (long id);

    //Optional<Comment> findCommentByArticle()

    //Buscar comentario segun el id
    Optional<Comment> findById(Long id);//ojo

    //Buscar comentario segun el id del articulo
    //Optional<Comment> findByArticle_Id(Long articleId);

    //Visualizar comentarios de un articulo especifico
    List<Comment> getByArticle_Id(Long articleId);

}
