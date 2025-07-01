package com.mohamed.backend.repository;

import com.mohamed.backend.model.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByCpr(String cpr);

    boolean existsByCpr(String cpr);
}
