package com.mohamed.backend.model.classinfo;

import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.topics.MainTopic;
import com.mohamed.backend.model.topics.SubTopic;
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
    @JoinColumn(name = "main_topic_id")
    private MainTopic mainTopic;

    @ManyToOne
    @JoinColumn(name = "sub_topic_id")
    private SubTopic subTopic;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class class_;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

}
