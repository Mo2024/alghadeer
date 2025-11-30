package com.mohamed.backend.salah.attempt.dto;

import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestion;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAndQuestionsDto {
    private StudentAttempt studentAttempt;
    private List<StudentSalahQuestion> salahQuestionsReq;
    private List<StudentSalahQuestionView> salahQuestionsRes;
}
