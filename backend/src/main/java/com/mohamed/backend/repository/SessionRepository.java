package com.mohamed.backend.repository;

import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.user.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

}
