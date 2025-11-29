package com.mohamed.backend.salah.attempt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mohamed.backend.salah.level.StudentLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "student_level_id")
    @JsonIgnoreProperties
    private StudentLevel studentLevel;

    @Column(name = "attempt_date_time")
    private LocalDateTime attemptDateTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Integer> subjects;

    @Column(name = "completed")
    private Boolean isCompleted;

    @Column(name = "passed")
    private Boolean isPassed;

    @Column(name = "comments")
    private String comments;

}
