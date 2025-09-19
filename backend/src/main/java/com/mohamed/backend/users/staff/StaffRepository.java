package com.mohamed.backend.users.staff;

import com.mohamed.backend.users.staff.dto.StaffListView;
import com.mohamed.backend.users.staff.dto.StaffView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    boolean existsByEmail(String email);

    Optional<Staff> findByEmailAndArchived(String email, Boolean isArchived);

    Optional<Staff> findByIdAndArchived(Integer id, Boolean isArchived);

    Page<StaffView> findAllByArchivedFalse(Pageable pageable);

    List<StaffListView> findAllByArchivedFalse();


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
