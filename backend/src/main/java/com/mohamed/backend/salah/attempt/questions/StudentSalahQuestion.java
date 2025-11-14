package com.mohamed.backend.salah.attempt.questions;

import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.questions.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_salah_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSalahQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_salah_question_seq")
    @SequenceGenerator(name = "student_salah_question_seq", sequenceName = "student_salah_question_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "evaluation")
    @Enumerated(EnumType.STRING)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "student_salah_attempt_id")
    private StudentAttempt studentSalahAttempt;

}
