package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.WriterResponseDTO;
import com.dk.dermokometicapi.services.WriterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/writers")
@AllArgsConstructor
public class WriterController {
    private final WriterService writerService;

    // get all writers
    @GetMapping()
    public ResponseEntity<List<WriterResponseDTO>> getAllWriters(){
        return new ResponseEntity<>(writerService.getAll(), HttpStatus.OK);
    }

    // get writer by id
    @GetMapping("/id/{id}")
    public ResponseEntity<WriterResponseDTO> getWriterById(@PathVariable Long id){
        return new ResponseEntity<>(writerService.getById(id), HttpStatus.OK);
    }
}
