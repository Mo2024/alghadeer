package com.mohamed.backend.salah.attempt.questions;

import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.questions.QuestionView;

public interface StudentSalahQuestionView {

//    default Integer getId() { return null; }
//    default Integer getGrade() { return null; }
//    default String getEvaluation() { return null; }

    Integer getId();
    Integer getGrade();
    String getEvaluation();
    QuestionView getQuestion();
    StudentAttempt getStudentSalahAttempt();
}
