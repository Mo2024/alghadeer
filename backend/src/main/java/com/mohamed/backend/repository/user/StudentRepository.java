package com.mohamed.backend.repository.user;

import com.mohamed.backend.model.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByCpr(String cpr);

    boolean existsByCpr(String cpr);

    int countByIdIn(List<Integer> studentsId);
}
