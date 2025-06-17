package com.mohamed.backend.repository;

import com.mohamed.backend.model.Class;
import com.mohamed.backend.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository  extends JpaRepository<Class, Integer> {
}
