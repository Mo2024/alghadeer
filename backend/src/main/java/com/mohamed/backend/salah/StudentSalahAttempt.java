package com.mohamed.backend.salah;

import com.mohamed.backend.salah.questions.Question;
import com.mohamed.backend.salah.questions.subjects.SubjectArea;
import com.mohamed.backend.users.students.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_salah_attempt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSalahAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_salah_attempt_seq")
    @SequenceGenerator(name = "student_salah_attempt_seq", sequenceName = "student_salah_attempt_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "grade")
    private Integer grade;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
