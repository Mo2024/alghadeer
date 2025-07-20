package com.mohamed.backend.security;

import com.mohamed.backend.model.enums.Permission;
import com.mohamed.backend.model.user.Student;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
public class StudentDetails implements UserDetails {

    @Autowired
    private Student student;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    public Integer getId(){ return student.getId(); }
    public Map<String, Boolean> getPermissions(){ return student.getPermissionBooleanMap(); };


    @Override public String getUsername() { return student.getEmail(); }
    @Override public String getPassword() { return student.getHash(); }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
