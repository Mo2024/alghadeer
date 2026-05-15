package com.mohamed.backend.topics.sub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Integer> {
    Optional<SubTopic> findByIdAndArchivedFalse(Integer id);
}
