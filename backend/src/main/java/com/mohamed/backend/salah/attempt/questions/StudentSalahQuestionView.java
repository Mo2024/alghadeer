package com.mohamed.backend.salah.attempt.questions;

import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.questions.QuestionView;

public interface StudentSalahQuestionView {

    Integer getId();
    Integer getGrade();
    String getEvaluation();
    QuestionView getQuestion();
    StudentAttempt getStudentSalahAttempt();
}
