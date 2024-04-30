package com.dk.dermokometicapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WriterResponseDTO {
    private Long id;
    private String name;
    private String lastName;
    private String profilePic;
    private String description;
}
