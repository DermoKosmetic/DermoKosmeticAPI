package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.WriterRequestDTO;
import com.dk.dermokometicapi.model.dto.WriterResponseDTO;
import com.dk.dermokometicapi.model.entity.Writer;
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
