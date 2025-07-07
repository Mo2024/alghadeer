package com.mohamed.backend.repository;

import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.user.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
}
