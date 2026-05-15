package com.mohamed.backend.topics.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicGroupRepository extends JpaRepository<TopicGroup, Integer> {
    List<TopicGroup> findAllByOrderByIdAsc();

    List<TopicGroup> findAllByArchivedFalseOrderByIdAsc();
}
