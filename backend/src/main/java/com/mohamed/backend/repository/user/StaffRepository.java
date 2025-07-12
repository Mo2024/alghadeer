package com.mohamed.backend.repository.user;

import com.mohamed.backend.model.user.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    boolean existsByEmail(String email);
    Optional<Staff> findByEmail(String email);

    @Query(value = """
    SELECT COUNT(*) > 0
    FROM staff_class_assignment
    WHERE staff_id = :staffId AND class_id = :classId
""", nativeQuery = true)
    boolean isAuthorizedToTakeAttendance(
            @Param("staffId") Integer staffId,
            @Param("classId") Integer classId
    );

    @Query(value = """
    SELECT COUNT(*) = 1
    FROM staff_permission
    WHERE staff_id = :staffId
      AND permission = 'INSTRUCTOR'
      AND (
        SELECT COUNT(*) FROM staff_permission sp2 WHERE sp2.staff_id = :staffId
      ) = 1
""", nativeQuery = true)
    boolean isInstructorOnly(@Param("staffId") Integer staffId);


}
