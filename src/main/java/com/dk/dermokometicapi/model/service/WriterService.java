package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.entity.Writer;
import com.dk.dermokometicapi.model.mapper.WriterMapper;
import com.dk.dermokometicapi.model.repository.WriterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WriterService {
    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;

    // Regular CRUD operations
    public List<Writer> getEntities(List<Long> ids) {
        return writerRepository.FindByIdList(ids);
    }
}
