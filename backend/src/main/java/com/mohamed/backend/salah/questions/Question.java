package com.mohamed.backend.salah.questions;

import com.mohamed.backend.salah.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questions_seq")
    @SequenceGenerator(name = "questions_seq", sequenceName = "questions_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "question")
    private String question;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "deleted")
    private Boolean deleted;
}
