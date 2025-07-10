package com.mohamed.backend.repository;

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

}
