package com.mohamed.backend.salah.attempt.questions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSalahQuestionRepository  extends JpaRepository<StudentSalahQuestion, Integer> {
}
