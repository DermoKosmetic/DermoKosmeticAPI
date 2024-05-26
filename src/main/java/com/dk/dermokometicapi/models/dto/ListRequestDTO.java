package com.dk.dermokometicapi.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListRequestDTO {

    @NotNull(message = "Order is mandatory")
    private String orderBy;

    @NotNull(message = "Page Size is mandatory")
    private int pageSize;

    @NotNull(message = "Page Number is mandatory")
    private int pageNum;
}
