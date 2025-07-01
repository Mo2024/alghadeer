package com.mohamed.backend.model.topics;

import com.mohamed.backend.model.classinfo.Session;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "main_topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "main_topic_seq")
    @SequenceGenerator(name = "main_topic_seq", sequenceName = "main_topic_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "mainTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTopic> subTopics = new ArrayList<>();

    @OneToMany(mappedBy = "mainTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();


}
