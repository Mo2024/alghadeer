package com.mohamed.backend.users.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.classes.Class;
import com.mohamed.backend.sessions.Session;
import com.mohamed.backend.users.staff.staffPermissions.Permission;
import com.mohamed.backend.users.staff.staffPermissions.StaffPermission;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staff_seq")
    @SequenceGenerator(name = "staff_seq", sequenceName = "staff_sequence", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "hash")
    private String hash;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffPermission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "staff")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Class> classes;

    @OneToMany(mappedBy = "staff")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Session> sessions;

    @Column(name = "is_archived")
    private Boolean archived;

    @Transient
    private Map<Permission, Boolean> permissionBooleanMap;

}
