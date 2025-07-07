package com.mohamed.backend.service;

import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.ClassSchedule;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public void createSessions(Class class_) {
        log.info("executing method [SessionService].[createSessions]");
        for (LocalDate startDate = class_.getSemester().getStartDate();
             !startDate.isAfter(class_.getSemester().getEndDate());
             startDate = startDate.plusDays(1))
        {

            for (ClassSchedule classSchedule : class_.getClassSchedules()){

                // Below check if the dayOfWeek from the outer loop is equal to dayOfWeek of class schedule
                if (startDate.getDayOfWeek().name().equals(classSchedule.getDayOfWeek().toString())) {
                    Session session = Session.builder()
                            .date(startDate)
                            .mainTopic(null)
                            .subTopic(null)
                            .semester(class_.getSemester())
                            .class_(class_)
                            .cancelled(false)
                            .build();

                    sessionRepository.save(session);
                    log.info("Session created successfully on {}", startDate);
                }

            }

        }
        log.info("[SessionService].[createSessions] executed successfully");
    }
}
