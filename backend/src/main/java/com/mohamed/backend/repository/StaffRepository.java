package com.mohamed.backend.repository;

import com.mohamed.backend.model.user.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    boolean existsByEmail(String email);
    Optional<Staff> findByEmail(String email);

}
