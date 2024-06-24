package com.dk.dermokometicapi.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class WriterInfoRequestDTO {
    @NotNull
    @NotEmpty
    List<Long> writerIds;
}
