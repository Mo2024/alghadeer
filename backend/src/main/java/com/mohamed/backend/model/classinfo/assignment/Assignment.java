package com.mohamed.backend.model.classinfo.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.classinfo.Class;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assignments_seq")
    @SequenceGenerator(name = "assignments_seq", sequenceName = "assignments_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Class class_;

    @Column(name = "total_grade")
    private Integer totalGrade;

    @Transient
    @JsonProperty("studentsAssignments")
    private List<StudentsAssignment> studentsAssignments;
}
