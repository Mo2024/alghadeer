package com.mohamed.backend.topics.sub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.topics.main.MainTopic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sub_topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_topic_seq")
    @SequenceGenerator(name = "sub_topic_seq", sequenceName = "sub_topic_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "archived")
    @JsonIgnore
    private Boolean archived;

    @ManyToOne
    @JoinColumn(name = "main_topic_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MainTopic mainTopic;

    @JsonProperty("mainTopicId")
    public Integer getMainTopicId() {
        return mainTopic != null ? mainTopic.getId() : null;
    }
}
