package com.mohamed.backend.users.students.dto;

import com.mohamed.backend.announcements.AnnouncementView;
import com.mohamed.backend.topics.main.MainTopicView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetailsPageDTO {
    private List<MainTopicView> missedTopics;
    private List<MainTopicView> attendedTopics;
    private Double attendancePercentage;
    private boolean isEnrolled;
    private List<AnnouncementView> announcements;
}
