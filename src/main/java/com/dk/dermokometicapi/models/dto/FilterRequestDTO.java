package com.dk.dermokometicapi.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequestDTO {
    private List<String> categories = new ArrayList<>();

    private String orderBy;
    @NotNull(message = "Page Size is mandatory")
    private int pageSize;

    @NotNull(message = "Page Number is mandatory")
    private int pageNum;
}
