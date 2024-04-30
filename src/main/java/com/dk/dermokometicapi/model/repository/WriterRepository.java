package com.dk.dermokometicapi.model.repository;

import com.dk.dermokometicapi.model.entity.Writer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface WriterRepository extends JpaRepository<Writer, Long> {
    // Regular CRUD operations

    List<Writer> FindByIdIn(List<Long> ids);
}
