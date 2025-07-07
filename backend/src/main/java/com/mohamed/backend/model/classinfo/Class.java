package com.mohamed.backend.model.classinfo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.user.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_seq")
    @SequenceGenerator(name = "class_seq", sequenceName = "class_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "classes")
    private List<Staff> staff;

    @OneToMany(mappedBy = "class_", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassSchedule> classSchedules = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Override
    public String toString() {
        return "Class{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", semester=" + (semester != null ? semester.getId() : null) +
                '}';
    }



}
