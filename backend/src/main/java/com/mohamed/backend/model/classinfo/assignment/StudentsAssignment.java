package com.mohamed.backend.model.classinfo.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.user.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "students_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentsAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "students_assignments_seq")
    @SequenceGenerator(name = "students_assignments_seq", sequenceName = "students_assignments_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Student student;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "assignment_done")
    private boolean assignmentDone;

    @JsonProperty("totalGrade")
    public Integer getTotalGrade() {
        return assignment != null ? assignment.getTotalGrade() : null;
    }

    @JsonProperty("studentName")
    public String getStudentName() {
        return student != null ? student.getName() : null;
    }

    @JsonProperty("studentId")
    public Integer getStudent_Id() {
        return student != null ? student.getId() : null;
    }

}
