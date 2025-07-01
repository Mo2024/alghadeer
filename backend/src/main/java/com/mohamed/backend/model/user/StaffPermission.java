package com.mohamed.backend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mohamed.backend.model.enums.Permission;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "staff")
public class StaffPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staff_perm_seq")
    @SequenceGenerator(name = "staff_perm_seq", sequenceName = "staff_perm_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @JsonIgnore
    private Staff staff;

}
