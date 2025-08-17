package com.mohamed.backend.model.announcement;

import com.mohamed.backend.model.classinfo.Class;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "announcement_targets")
@Getter
@Setter
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
    private Announcement announcement;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class semesterClass;

    @Column(name = "is_general")
    private boolean isGeneral;

}
