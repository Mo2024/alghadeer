package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.user.Staff;
import com.mohamed.backend.repository.ClassRepository;
import com.mohamed.backend.repository.ClassScheduleRepository;
import com.mohamed.backend.repository.StaffRepository;
import com.mohamed.backend.utils.Defaults;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Transactional
    public void createDefaultClasses(Semester semester){
        log.info("executing method [ClassService].[createDefaultClasses]");

        List<Class> classes = Defaults.getDefaultClasses(semester);
        classRepository.saveAll(classes);

        log.info("Classes created successfully:\n {}", classes);
        log.info("[ClassService].[createDefaultClasses] executed successfully");
    }

    @Transactional
    public void createCustomClasses(List<Class> classes, Semester semester) {
        log.info("Executing method [ClassService].[createCustomClasses]");
        log.info("Request Parameter:\nClasses:\n{}\nSemester\n{}", classes, semester);

        for (Class class_ : classes) {
            if (class_.getName() == null || class_.getName().trim().isEmpty() || !ValidationUtils.isArabic(class_.getName())) {
                log.error("Invalid name:\n{}", class_.getName());
                throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
            }

            if (class_.getStaff() == null || class_.getStaff().isEmpty()) {
                throw new UnhandledRejection("يجب تحديد الطاقم");
            }

            List<Integer> staffIds = class_.getStaff().stream()
                    .map(Staff::getId)
                    .collect(Collectors.toList());

            List<Staff> validStaff = staffRepository.findAllById(staffIds);

            if (validStaff.size() != staffIds.size()) {
                log.error("Invalid staff provided:\n{}", class_.getStaff());
                throw new UnhandledRejection("يوجد طاقم غير صالح أو غير موجود");
            }

            if (class_.getClassSchedules() == null || class_.getClassSchedules().stream()
                    .anyMatch(classSchedule -> ValidationUtils.validateSchedule(
                            classSchedule.getDayOfWeek(), classSchedule.getStartTime(), classSchedule.getEndTime()))) {
                log.error("Invalid schedules:\n{}", class_.getClassSchedules());
                throw new UnhandledRejection("يوجد جدول زمني غير صالح في الفصل");
            }

            class_.getClassSchedules().forEach(schedule -> schedule.setClass_(class_));
            class_.setSemester(semester);

            Class savedClass = classRepository.save(class_);

            validStaff.forEach(staff -> {
                staff.addClass(savedClass);
            });

            staffRepository.saveAll(validStaff);

            class_.setStaff(validStaff);

            log.info("Class Created Successfully:\n {}", savedClass);
        }

        log.info("[ClassService].[createCustomClasses] executed successfully");
    }



}
