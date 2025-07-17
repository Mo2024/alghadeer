package com.mohamed.backend.model.classinfo;

import com.mohamed.backend.model.enums.Grade;
import com.mohamed.backend.model.semester.Semester;
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
    @SequenceGenerator(name = "grade_class_assignment_seq", sequenceName = "grade_class_assignment_sequence", initialValue = 13, allocationSize = 1)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class class_;

}
