package com.mohamed.backend.repository;

import com.mohamed.backend.model.Staff;
import com.mohamed.backend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {
    boolean existsByEmail(String email);
    Optional<Staff> findByEmail(String email);

}
