package com.mohamed.backend.model.announcement;

import com.mohamed.backend.model.classinfo.assignment.Assignment;
import com.mohamed.backend.model.enums.AnnouncementType;
import com.mohamed.backend.model.semester.Semester;
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

    @Column(name = "star_date_time")
    private LocalDateTime startDate;

    @Column(name = "end_date_time")
    private LocalDateTime endDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnouncementTarget> targets;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
