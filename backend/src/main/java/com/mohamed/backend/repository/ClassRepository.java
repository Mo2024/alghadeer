package com.mohamed.backend.repository;

import com.mohamed.backend.model.classinfo.Class;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository  extends JpaRepository<Class, Integer> {
}
