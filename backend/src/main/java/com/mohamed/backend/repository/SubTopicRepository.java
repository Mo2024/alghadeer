package com.mohamed.backend.repository;

import com.mohamed.backend.model.semester.SemesterEnrollment;
import com.mohamed.backend.model.topics.SubTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Integer> {
}
