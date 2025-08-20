package com.mohamed.backend.dto.user;

import com.mohamed.backend.dto.topic.MainTopicView;
import com.mohamed.backend.model.announcement.Announcement;
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
    private List<Announcement> announcements;
}
