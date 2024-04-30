package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;


public interface WriterRepository extends JpaRepository<Writer, Long> {
    // Regular CRUD operations

    @Query("SELECT w FROM Writer w WHERE w.id IN :ids")
    List<Writer> FindByIdList(List<Long> ids);
}
