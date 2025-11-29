package com.mohamed.backend.salah.questions.subjects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subject_area")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectArea {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_area_seq")
    @SequenceGenerator(name = "subject_area_seq", sequenceName = "subject_area_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

}
