package com.mohamed.backend.salah.attempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAttemptRepository extends JpaRepository<StudentAttempt, Integer> {
}
