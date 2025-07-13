package com.mohamed.backend.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mohamed.backend.model.classinfo.Class;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "staff_class_assignment",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<Class> classes;

    @Column(name = "is_archived")
    private Boolean archived;

    public void addClass(Class newClass) {
        if (this.classes == null) {
            this.classes = new ArrayList<>();
        }
        this.classes.add(newClass);
    }


}
