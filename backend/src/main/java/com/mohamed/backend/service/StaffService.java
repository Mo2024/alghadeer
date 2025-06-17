package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.model.Staff;
import com.mohamed.backend.repository.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public Page<Staff> getStaff(Pageable pageable){
        return staffRepository.findAll(pageable);
    }

    @Transactional
    public Response register(Staff staffRequest){
        log.info("Registering staff: {}", staffRequest);

        return new Response("a");

    }
}
