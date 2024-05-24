package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.AnswerRequestDTO;
import com.dk.dermokometicapi.models.dto.AnswerResponseDTO;
import com.dk.dermokometicapi.models.entity.Answer;
import com.dk.dermokometicapi.models.entity.Question;
import com.dk.dermokometicapi.models.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class AnswerMapper {

    private final ModelMapper modelMapper;

    public Answer convertToEntity(AnswerRequestDTO answerRequestDTO, User user, Question question, Answer parentAnswer) {
        Answer answer = new Answer();
        answer.setContent(answerRequestDTO.getContent());
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setParentAnswer(parentAnswer);
        answer.setPublicationDate(LocalDate.now());
        return answer;
    }

    public AnswerResponseDTO convertToDTO(Answer answer, Long likes, Long responses) {
        AnswerResponseDTO answerResponseDTO = modelMapper.map(answer, AnswerResponseDTO.class);
        answerResponseDTO.setLikes(likes);
        answerResponseDTO.setResponses(responses);
        return answerResponseDTO;
    }
}
