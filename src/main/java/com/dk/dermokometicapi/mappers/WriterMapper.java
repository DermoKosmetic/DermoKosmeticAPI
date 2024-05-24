package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.WriterRequestDTO;
import com.dk.dermokometicapi.models.dto.WriterResponseDTO;
import com.dk.dermokometicapi.models.entity.Writer;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WriterMapper {
    private final ModelMapper modelMapper;

    public Writer convertToEntity(WriterRequestDTO writerRequestDTO) {
        return modelMapper.map(writerRequestDTO, Writer.class);
    }

    public WriterResponseDTO convertToDTO(Writer writer) {
        return modelMapper.map(writer, WriterResponseDTO.class);
    }
}
