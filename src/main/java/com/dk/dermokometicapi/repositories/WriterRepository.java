package com.dk.dermokometicapi.repositories;

import com.dk.dermokometicapi.models.entity.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface WriterRepository extends JpaRepository<Writer, Long> {
    // Regular CRUD operations

    @Query("SELECT w FROM Writer w WHERE w.id IN :ids")
    List<Writer> FindByIdList(List<Long> ids);
}
