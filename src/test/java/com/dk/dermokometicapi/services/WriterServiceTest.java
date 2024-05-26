package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.models.dto.WriterResponseDTO;
import com.dk.dermokometicapi.models.entities.Writer;
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

    @Test
    public void testGetAll(){
        // Arrange
        Writer writer1 = new Writer(); writer1.setId(1L);
        Writer writer2 = new Writer(); writer2.setId(2L);
        Writer writer3 = new Writer(); writer3.setId(3L);
        List<Writer> writers = Arrays.asList(writer1, writer2, writer3);
        when(writerRepository.findAll()).thenReturn(writers);

        WriterResponseDTO writerResponseDTO1 = new WriterResponseDTO(); writerResponseDTO1.setId(1L);
        WriterResponseDTO writerResponseDTO2 = new WriterResponseDTO(); writerResponseDTO2.setId(2L);
        WriterResponseDTO writerResponseDTO3 = new WriterResponseDTO(); writerResponseDTO3.setId(3L);
        when(writerMapper.convertToDTO(writer1)).thenReturn(writerResponseDTO1);
        when(writerMapper.convertToDTO(writer2)).thenReturn(writerResponseDTO2);
        when(writerMapper.convertToDTO(writer3)).thenReturn(writerResponseDTO3);

        // Act
        List<WriterResponseDTO> result = writerService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    public void testGetById(){
        // Arrange
        Writer writer = new Writer(); writer.setId(1L);
        when(writerRepository.findById(1L)).thenReturn(java.util.Optional.of(writer));

        WriterResponseDTO writerResponseDTO = new WriterResponseDTO(); writerResponseDTO.setId(1L);
        when(writerMapper.convertToDTO(writer)).thenReturn(writerResponseDTO);

        // Act
        WriterResponseDTO result = writerService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetByIdNotFound(){
        // Arrange
        when(writerRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act
        try {
            writerService.getById(1L);
        } catch (RuntimeException e) {
            // Assert
            assertEquals("Writer not found", e.getMessage());
        }
    }
}
