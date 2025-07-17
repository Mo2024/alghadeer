package com.mohamed.backend.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.enums.Grade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "student_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "area")
    private String area;

    @Column(name = "name")
    private String name;

    @Column(name = "cpr")
    private String cpr;

    @Column(name = "hash")
    private String hash;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ManyToMany
    @JoinTable(
            name = "student_class",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<Class> classes;

    public void addClass(Class class_) {
        if (!classes.contains(class_)) {
            classes.add(class_);
            class_.getStudents().add(this); // also update the other side
        }
    }

}
