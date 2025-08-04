package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.security.StaffDetails;
import com.mohamed.backend.security.StudentDetails;
import com.mohamed.backend.utils.Logger;
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

    @Autowired
    private Logger logger;

    public Map<?, Boolean> getAuthentication() throws JsonProcessingException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        logger.logJsonObject("Fetching authentication details:\n{}", auth);


        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            Map<?, Boolean> permissions;

            if (principal instanceof StudentDetails userDetails) {
                permissions = userDetails.getPermissions();
            } else {
                permissions = ((StaffDetails) principal).getPermissions();
            }

            log.info("User is authenticated successfully");
            return permissions;
        } else {
            log.error("Unauthorized access");
            throw new AuthorizationDeniedException("Access Denied");
        }
    }

    public Response logout(HttpSession session) {

        SecurityContextHolder.clearContext();

        if (session != null) {
            session.invalidate();
        }

        return new Response("تم تسجيل الخروج بنجاح");
    }

}
