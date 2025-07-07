package com.mohamed.backend.repository;

import com.mohamed.backend.model.classinfo.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer> {
}
