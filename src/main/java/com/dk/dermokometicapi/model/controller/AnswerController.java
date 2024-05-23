package com.dk.dermokometicapi.model.controller;

import com.dk.dermokometicapi.model.dto.*;
import com.dk.dermokometicapi.model.service.AnswerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
@AllArgsConstructor
public class AnswerController {
    public final AnswerService answerService;

    // get all answers
    @GetMapping()
    public ResponseEntity<List<AnswerResponseDTO>> getAllAnswers(){
        return new ResponseEntity<>(answerService.getAllAnswers(), HttpStatus.OK);
    }

    // get answer by id
    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> getAnswerById(@PathVariable Long id){
        return new ResponseEntity<>(answerService.getAnswerById(id), HttpStatus.OK);
    }

    // create answer
    @PostMapping()
    public ResponseEntity<AnswerResponseDTO> addAnswer(@RequestBody @Valid AnswerRequestDTO answerRequestDTO){
        return new ResponseEntity<>(answerService.addAnswer(answerRequestDTO), HttpStatus.CREATED);
    }

    // get answers by question id
    @GetMapping("/question/{id}")
    public ResponseEntity<Page<AnswerResponseDTO>> getAnswersByQuestionId(@PathVariable Long id, @RequestBody @Valid ListRequestDTO listRequestDTO){
        return new ResponseEntity<>(answerService.getAnswersByQuestionId(id, listRequestDTO), HttpStatus.OK);
    }

    // get answers by parent id
    @GetMapping("/parent/{id}")
    public ResponseEntity<Page<AnswerResponseDTO>> getAnswersByParentId(@PathVariable Long id, @RequestBody @Valid ListRequestDTO listRequestDTO){
        return new ResponseEntity<>(answerService.getAnswersByParentId(id, listRequestDTO), HttpStatus.OK);
    }

    // delete answer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnswer(@PathVariable Long id){
        answerService.deleteAnswer(id);
        return new ResponseEntity<>("Answer deleted successfully", HttpStatus.OK);
    }

    // like answer
    @PostMapping("/like")
    public ResponseEntity<AnswerLikeResponseDTO> likeAnswer(@RequestBody @Valid AnswerLikeRequestDTO answerLikeRequestDTO){
        return new ResponseEntity<>(answerService.likeAnswer(answerLikeRequestDTO), HttpStatus.OK);
    }

    // unlike answer
    @DeleteMapping("/like")
    public ResponseEntity<String> unlikeAnswer(@RequestBody @Valid AnswerLikeRequestDTO answerLikeRequestDTO){
        answerService.unlikeAnswer(answerLikeRequestDTO);
        return new ResponseEntity<>("Like deleted successfully", HttpStatus.OK);
    }

}
