package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.*;
import com.mohamed.backend.model.enums.Permission;
import com.mohamed.backend.model.user.StaffPermission;
import com.mohamed.backend.security.StaffDetails;
import com.mohamed.backend.utils.*;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.user.Staff;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.user.StaffRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SimpleEmail simpleEmail;

    @Autowired
    private Logger logger;

    public Integer getStaffId() {
        StaffDetails staffDetails = (StaffDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return staffDetails.getId();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public Page<StaffView> getStaff(Pageable pageable) {
        return staffRepository.findAllByArchivedFalse(pageable);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public List<StaffListView> getStaff() {
        return staffRepository.findAllByArchivedFalse();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Response register(Staff staffRequest) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", staffRequest);

        if (staffRequest.getName() == null || staffRequest.getName().trim().isEmpty() || !ValidationUtils.isArabic(staffRequest.getName())) {
            log.error("Invalid name:\n{}", staffRequest.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (staffRequest.getEmail() == null || staffRequest.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(staffRequest.getEmail())) {
            log.error("Invalid email:\n{}", staffRequest.getEmail());
            throw new HandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        log.info("Calling [staffRepository].[existsByEmail]");
        if (staffRepository.existsByEmail(staffRequest.getEmail())) {
            throw new HandledRejection("هذا البريد الإلكتروني مستخدم مسبقاً");
        }
        log.info("[staffRepository].[existsByEmail] called successfully");

        String password = String.valueOf(RandomNumberGenerator.generate8DigitNumber());
        String cleanEmail = staffRequest.getEmail().trim().toLowerCase();

        List<StaffPermission> staffPermissionList = new ArrayList<>();

        for (StaffPermission perm : staffRequest.getPermissions()) {
            if (perm == null || perm.getPermission() == null) {
                throw new HandledRejection("يرجى التأكد من أن جميع الصفوف المحددة صحيحة");
            }
            staffPermissionList.add(perm);
        }

        logger.logJsonObject("Permissions list:\n{}", staffPermissionList);

        Staff staff = Staff.builder()
                .name(staffRequest.getName())
                .email(cleanEmail)
                .hash(HashUtils.sha256(password))
                .archived(false)
                .build();


        staffPermissionList.forEach(perm -> perm.setStaff(staff));
        staff.setPermissions(staffPermissionList);
        log.info("Calling [staffRepository].[save]");
        staffRepository.save(staff);
        log.info("[staffRepository].[save] called successfully");

        logger.logJsonObject("Staff saved to DB successfully:\n{}", staff);

        String to = staff.getEmail();
        String subject = "كلمة المرور لحسابك الجديد";  // "Password for your new account" in Arabic
        String body = "مرحباً،\n\n" +
                "كلمة المرور الخاصة بحسابك الجديد هي: " + password + "\n" +
                "يرجى الاحتفاظ بها وعدم مشاركتها مع أي شخص.\n\n" +
                "مع تحياتنا.";

        try {
            simpleEmail.sendSimpleEmail(to, subject, body);
            log.info("Password sent to email successfully");
        } catch (Exception e) {
            log.error("Failed to send email to\n{}", to);
            log.error(e.getMessage());
            throw new HandledRejection("حدث خطأ أثناء إرسال البريد الإلكتروني");
        }

        log.info("Registration successful");

        return new Response("تم التسجيل بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response changeEmail(ChangeEmail newEmailReq) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", newEmailReq);

        if (newEmailReq.getEmail() == null || newEmailReq.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(newEmailReq.getEmail())) {
            log.error("Invalid email:\n{}", newEmailReq.getEmail());
            throw new HandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        log.info("Calling [staffRepository].[existsByEmail]");
        if (staffRepository.existsByEmail(newEmailReq.getEmail())) {
            throw new HandledRejection("البريد الإلكتروني مستخدم بالفعل");
        }
        log.info("[staffRepository].[existsByEmail] called successfully");

        log.info("Calling [staffRepository].[findByIdAndArchived]");
        Staff staff = staffRepository.findByIdAndArchived(getStaffId(), false)
                .orElseThrow(() -> {
                    log.error("Staff not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[staffRepository].[findByIdAndArchived] called successfully");

        String cleanEmail = newEmailReq.getEmail().trim().toLowerCase();

        logger.logJsonObject("old staff object:\n{}", staff);
        staff.setEmail(cleanEmail);
        logger.logJsonObject("new staff object:\n{}", staff);
        log.info("Calling [staffRepository].[save]");
        staffRepository.save(staff);
        log.info("[staffRepository].[save] called successfully");
        return new Response("تم تغيير البريد الإلكتروني بنجاح");
    }

    public Response login(Login login, HttpSession session) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", login);


        login.setUsername(login.getUsername().trim().toLowerCase());

        log.info("Calling [staffRepository].[findByEmailAndArchived]");
        Staff staff = staffRepository.findByEmailAndArchived(login.getUsername(), false)
                .orElseThrow(() -> {
                    log.error("Staff not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[staffRepository].[findByEmailAndArchived] called successfully");

        StaffDetails staffDetails = new StaffDetails(staff);

        logger.logJsonObject("Staff info:\n{}", staff);

        if (login.getUsername() == null || login.getPassword() == null ||
                login.getUsername().isBlank() || login.getPassword().isBlank()) {
            log.error("Invalid login input");
            throw new HandledRejection("الرجاء إدخال اسم المستخدم وكلمة المرور");
        } else if (!staff.getHash().equals(HashUtils.sha256(login.getPassword()))) {
            log.error("Invalid login attempt");
            throw new HandledRejection("البريد الإلكتروني أو كلمة المرور غير صحيحة");
        } else if (staff.getHash().equals(HashUtils.sha256(login.getPassword()))) {
            Map<Permission, Boolean> permissionBooleanMap = new HashMap<>();
            staff.getPermissions().forEach(staffPermission -> permissionBooleanMap.put(staffPermission.getPermission(), true));
            staff.setPermissionBooleanMap(permissionBooleanMap);

            Authentication authentication = new UsernamePasswordAuthenticationToken(staffDetails, null, staffDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            log.info("Staff ID: {} logged in", staff.getId());
        }
        return new Response("تم تسجيل الدخول بنجاح", staffDetails.getPermissions());
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Page<StaffView> archiveStaff(ArchiveDto archiveDto) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", archiveDto);

        log.info("Calling [staffRepository].[findByIdAndArchived]");
        Staff staffObj = staffRepository.findByIdAndArchived(archiveDto.getStaff().getId(), false)
                .orElseThrow(() -> {
                    log.error("Staff not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[staffRepository].[findByIdAndArchived] called successfully");

        if (staffObj.getId().equals(getStaffId())) {
            log.error("Staff tried to archive himself");
            throw new HandledRejection("لا يمكنك أرشفة نفسك");
        }

        staffObj.setArchived(true);
        log.info("Calling [staffRepository].[save]");
        staffRepository.save(staffObj);
        log.info("[staffRepository].[save] called successfully");

        Pageable pageable = PageRequest.of(archiveDto.getPage(), archiveDto.getSize());
        return getStaff(pageable);
    }

}
