package com.dk.dermokometicapi.model.controller;

import com.dk.dermokometicapi.model.dto.QuestionRequestDTO;
import com.dk.dermokometicapi.model.dto.QuestionResponseDTO;
import com.dk.dermokometicapi.model.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }


    @Transactional
    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Validated @RequestBody QuestionRequestDTO questionRequestDTO) {
        return ResponseEntity.ok(questionService.createQuestion(questionRequestDTO));
    }

}
