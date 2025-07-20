package com.mohamed.backend.model.user;

import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.enums.Permission;
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
    private List<Class> classes;

    @OneToMany(mappedBy = "staff")
    private List<Session> sessions;

    @Column(name = "is_archived")
    private Boolean archived;

    @Transient
    private Map<Permission, Boolean> permissionBooleanMap;

}
