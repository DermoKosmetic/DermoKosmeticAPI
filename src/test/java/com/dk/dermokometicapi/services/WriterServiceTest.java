package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.models.entity.Writer;
import com.dk.dermokometicapi.mappers.WriterMapper;
import com.dk.dermokometicapi.repositories.WriterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WriterServiceTest {

    @Mock
    private WriterRepository writerRepository;

    @Mock
    private WriterMapper writerMapper;

    @InjectMocks
    private WriterService writerService;

    @Test
    public void testGetEntities() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Writer writer1 = new Writer(); writer1.setId(1L);
        Writer writer2 = new Writer(); writer2.setId(2L);
        Writer writer3 = new Writer(); writer3.setId(3L);
        List<Writer> writers = Arrays.asList(writer1, writer2, writer3);
        when(writerRepository.FindByIdList(ids)).thenReturn(writers);

        // Act
        List<Writer> result = writerService.getEntities(ids);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }
}
