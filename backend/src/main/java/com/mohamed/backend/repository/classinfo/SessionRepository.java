package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.model.classinfo.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {


    @Query(value = """
    SELECT COUNT(*) > 0
    FROM sessions
    WHERE staff_id = :staffId AND class_id = :classId
""", nativeQuery = true)
    boolean isAuthorizedToTakeAttendance(
            @Param("staffId") Integer staffId,
            @Param("classId") Integer classId
    );

}
