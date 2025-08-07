package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.model.classinfo.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    int countAttendanceBySessionId(int sessionId);

    List<Attendance> findBySessionId(int sessionId);
}
