package com.mohamed.backend.topics.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MainTopicRepository extends JpaRepository<MainTopic, Integer> {
    List<MainTopic> findAllByOrderByIdAsc();

    Optional<MainTopic> findByIdAndArchivedFalse(Integer id);
}
