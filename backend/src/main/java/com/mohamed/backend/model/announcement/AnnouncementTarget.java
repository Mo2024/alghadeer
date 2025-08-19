package com.mohamed.backend.model.announcement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.classinfo.Class;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "announcement_targets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_target_seq")
    @SequenceGenerator(name = "announcement_target_seq", sequenceName = "announcement_target_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Announcement announcement;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Class semesterClass;

    @JsonProperty("classId")
    public Integer getClass_Id() {
        return semesterClass != null ? semesterClass.getId() : null;
    }

    @JsonProperty("announcementId")
    public Integer getAnnouncement_id() {
        return announcement != null ? announcement.getId() : null;
    }

}
