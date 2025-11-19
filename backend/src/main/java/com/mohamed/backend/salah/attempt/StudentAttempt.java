package com.mohamed.backend.salah.attempt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.users.students.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_salah_attempt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_salah_attempt_seq")
    @SequenceGenerator(name = "student_salah_attempt_seq", sequenceName = "student_salah_attempt_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties
    private Student student;

    @Column(name = "attempt_date_time")
    private LocalDateTime attemptDateTime;

    @JsonProperty("studentId")
    public Integer getStudent() {
        return student != null ? student.getId() : null;
    }


}
