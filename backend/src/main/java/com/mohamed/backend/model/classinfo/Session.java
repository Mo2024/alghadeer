package com.mohamed.backend.model.classinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.topics.SubTopic;
import com.mohamed.backend.model.user.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessions_seq")
    @SequenceGenerator(name = "sessions_seq", sequenceName = "sessions_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "sub_topic_id")
    private SubTopic subTopic;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonProperty("class")
    private Class semesterClass;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @Column(name = "cancelled")
    private Boolean cancelled;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", date=" + date +
                ", subTopic=" + (subTopic != null ? subTopic.getId() : null) +
                ", semester=" + (semester != null ? semester.getId() : null) +
                ", class_=" + (semesterClass != null ? semesterClass.getId() : null) +
                ", cancelled=" + cancelled +
                '}';
    }


}
