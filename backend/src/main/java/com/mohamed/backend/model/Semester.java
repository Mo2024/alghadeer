package com.mohamed.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "semester_seq")
    @SequenceGenerator(name = "semester_seq", sequenceName = "semester_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "semester")
    @Enumerated(EnumType.STRING)
    private SemesterList semester;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "active")
    private Boolean active;

}
