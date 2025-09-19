package com.mohamed.backend.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.assignments.Assignment;
import com.mohamed.backend.classes.classesSchedules.ClassSchedule;
import com.mohamed.backend.classes.gradeClassAssignments.GradeClassAssignment;
import com.mohamed.backend.sessions.Session;
import com.mohamed.backend.semesters.Semester;
import com.mohamed.backend.users.staff.Staff;
import com.mohamed.backend.users.students.Student;
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

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToMany(mappedBy = "classes")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Student> students;

    @OneToMany(mappedBy = "class_", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassSchedule> classSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "semesterClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OrderBy("date ASC")
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "class_", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDateTime DESC")
    private List<Assignment> assignments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Transient
    @JsonProperty("gradeClassAssignments")
    private List<GradeClassAssignment> gradeClassAssignments;

    @Override
    public String toString() {
        return "Class{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", semester=" + (semester != null ? semester.getId() : null) +
               '}';
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            student.getClasses().add(this);
        }
    }

}
