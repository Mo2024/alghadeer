package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.model.classinfo.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

}
