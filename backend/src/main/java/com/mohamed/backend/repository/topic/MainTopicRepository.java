package com.mohamed.backend.repository.topic;

import com.mohamed.backend.model.topics.MainTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainTopicRepository extends JpaRepository<MainTopic, Integer> {
}
