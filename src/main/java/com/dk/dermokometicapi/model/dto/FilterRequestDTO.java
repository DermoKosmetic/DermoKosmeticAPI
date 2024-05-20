package com.dk.dermokometicapi.model.dto;

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
    @NotNull
    private int pageSize;

    @NotNull
    private int pageNum;
}
