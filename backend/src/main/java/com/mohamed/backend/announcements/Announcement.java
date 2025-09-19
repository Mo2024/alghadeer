package com.mohamed.backend.announcements;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.announcements.annnouncementTarget.AnnouncementTarget;
import com.mohamed.backend.announcements.annnouncementTarget.AnnouncementType;
import com.mohamed.backend.assignments.Assignment;
import com.mohamed.backend.semesters.Semester;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_seq")
    @SequenceGenerator(name = "announcement_seq", sequenceName = "announcement_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "announcement_type")
    private AnnouncementType announcementType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "start_date_time")
    private LocalDateTime startDate;

    @Column(name = "end_date_time")
    private LocalDateTime endDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnouncementTarget> announcementTargets;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Column(name = "is_general")
    private boolean isGeneral;

    @Column(name = "is_cancelled")
    private boolean isCancelled;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
