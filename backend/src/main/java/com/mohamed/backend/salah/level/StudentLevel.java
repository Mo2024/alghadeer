package com.mohamed.backend.salah.level;

import com.mohamed.backend.salah.questions.Level;
import com.mohamed.backend.users.students.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_level")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_level_seq")
    @SequenceGenerator(name = "student_level_seq", sequenceName = "student_level_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;
}
