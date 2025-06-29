package com.mohamed.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student_enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_enrollments_seq")
    @SequenceGenerator(name = "student_enrollments_seq", sequenceName = "student_enrollments_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;
}
