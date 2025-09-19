package com.mohamed.backend.classes.gradeClassAssignments;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.classes.Class;
import com.mohamed.backend.semesters.Semester;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grade_class_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeClassAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grade_class_assignment_seq")
    @SequenceGenerator(name = "grade_class_assignment_seq", sequenceName = "grade_class_assignment_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Class class_;

}
