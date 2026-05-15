package com.mohamed.backend.topics.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.topics.group.TopicGroup;
import com.mohamed.backend.topics.sub.SubTopic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

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

    @Column(name = "archived")
    @JsonIgnore
    private Boolean archived;

    @OneToMany(mappedBy = "mainTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @SQLRestriction("archived = false")
    private List<SubTopic> subTopics = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "topics_groups_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TopicGroup topicGroup;

}
