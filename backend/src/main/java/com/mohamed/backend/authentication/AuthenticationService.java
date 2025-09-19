package com.mohamed.backend.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.security.StaffDetails;
import com.mohamed.backend.utils.security.StudentDetails;
import com.mohamed.backend.utils.methods.Logger;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final Logger logger;

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
