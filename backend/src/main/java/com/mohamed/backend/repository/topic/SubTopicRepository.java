package com.mohamed.backend.repository.topic;

import com.mohamed.backend.model.topics.SubTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Integer> {
}
