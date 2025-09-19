package com.mohamed.backend.topics.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainTopicRepository extends JpaRepository<MainTopic, Integer> {
    List<MainTopic> findAllByOrderByIdAsc();
}
