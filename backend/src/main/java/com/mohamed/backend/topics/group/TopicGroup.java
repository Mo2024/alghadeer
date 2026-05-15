package com.mohamed.backend.topics.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mohamed.backend.topics.main.MainTopic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topics_groups_seq")
    @SequenceGenerator(name = "topics_groups_seq", sequenceName = "topics_groups_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "archived")
    @JsonIgnore
    private Boolean archived;

    @OneToMany(mappedBy = "topicGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @SQLRestriction("archived = false")
    private List<MainTopic> mainTopics = new ArrayList<>();

}
