package com.mohamed.backend.model.classinfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.user.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_seq")
    @SequenceGenerator(name = "attendance_seq", sequenceName = "attendance_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Student student;

    @Column(name = "is_present")
    private Boolean isPresent;

}
