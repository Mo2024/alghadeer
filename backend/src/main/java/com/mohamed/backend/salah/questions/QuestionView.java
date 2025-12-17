package com.mohamed.backend.salah.questions;

import com.mohamed.backend.salah.questions.subjects.Subject;
import com.mohamed.backend.salah.questions.subjects.SubjectArea;

public interface QuestionView {

    Integer getId();
    String getQuestion();
    Integer getSequence();
    Boolean getIsPillar();
    Integer getSubjectId();
    Subject getSubject();
    SubjectArea getArea();
}
