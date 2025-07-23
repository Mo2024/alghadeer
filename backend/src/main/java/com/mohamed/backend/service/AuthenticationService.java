package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.security.StaffDetails;
import com.mohamed.backend.security.StudentDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffService staffService;

    public Map<?, Boolean> getAuthentication(){
        log.info("executing method [AuthenticationService].[getAuthentication]");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("Fetching authentication details: \n {}", auth);

        if(auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)){
            Object principal = auth.getPrincipal();
            Map<?, Boolean> permissions;

            if(principal instanceof StudentDetails userDetails){
                permissions = userDetails.getPermissions();
            } else {
                permissions = ((StaffDetails) principal).getPermissions();
            }

            log.info("User is authenticated successfully");
            log.info("[AuthenticationService].[getAuthentication] executed successfully");
            return permissions;
        } else {
            log.error("Unauthorized access");
            throw new AuthorizationDeniedException("Access Denied");
        }
    }

    public Response logout(HttpSession session) {
        log.info("executing method [StudentService].[logout]");

        SecurityContextHolder.clearContext();

        if (session != null) {
            session.invalidate();
        }

        log.info("[StudentService].[logout] executed successfully");
        return new Response("تم تسجيل الخروج بنجاح");
    }

}
