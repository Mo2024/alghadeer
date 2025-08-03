package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.dto.SessionView;
import com.mohamed.backend.model.classinfo.Session;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

    List<Session> findByIdIn(List<Integer> ids);

    @Query(value = """
    SELECT COUNT(*) = 1
    FROM sessions
    WHERE staff_id = :staffId AND id = :sessionId
""", nativeQuery = true)
    boolean isAuthorizedToTakeAttendanceForSession(
            @Param("staffId") Integer staffId,
            @Param("sessionId") Integer sessionId
    );

    List<SessionView> findAllByStaffIdAndDateGreaterThanEqual(Integer staffId, LocalDate date);

}
