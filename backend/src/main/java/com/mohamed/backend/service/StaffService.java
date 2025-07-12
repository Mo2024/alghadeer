package com.mohamed.backend.service;

import com.mohamed.backend.dto.Login;
import com.mohamed.backend.model.user.StaffPermission;
import com.mohamed.backend.security.StaffDetails;
import com.mohamed.backend.utils.HashUtils;
import com.mohamed.backend.utils.RandomNumberGenerator;
import com.mohamed.backend.utils.SimpleEmail;
import com.mohamed.backend.utils.ValidationUtils;
import com.mohamed.backend.dto.ChangeEmail;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.user.Staff;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.user.StaffRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SimpleEmail simpleEmail;

    public Integer getStaffId() {
        StaffDetails staffDetails = (StaffDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return staffDetails.getId();
    }


    public Page<Staff> getStaff(Pageable pageable){
        return staffRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Response register(Staff staffRequest){
        log.info("executing method [StaffService].[register]");

        log.info("Staff info:\n{}", staffRequest);

        if (staffRequest.getName() == null || staffRequest.getName().trim().isEmpty() || !ValidationUtils.isArabic(staffRequest.getName())) {
            log.error("Invalid name:\n{}", staffRequest.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (staffRequest.getEmail() == null || staffRequest.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(staffRequest.getEmail())) {
            log.error("Invalid email:\n{}", staffRequest.getEmail());
            throw new UnhandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        if (staffRepository.existsByEmail(staffRequest.getEmail())) {
            throw new UnhandledRejection("هذا البريد الإلكتروني مستخدم مسبقاً");
        }


        //Validates that the class actually exists
        for (Class cls : staffRequest.getClasses()) {
            if (cls == null || cls.getId() == null || cls.getId() <= 0 || !classRepository.existsById(cls.getId())) {
                throw new UnhandledRejection("يرجى التأكد من أن جميع الصفوف المحددة صحيحة");
            }
        }

        String password = String.valueOf(RandomNumberGenerator.generate8DigitNumber());
        String cleanEmail = staffRequest.getEmail().trim();

        List<StaffPermission> staffPermissionList = new ArrayList<>();

        for (StaffPermission perm : staffRequest.getPermissions()){
            if (perm == null || perm.getPermission() == null) {
                throw new UnhandledRejection("يرجى التأكد من أن جميع الصفوف المحددة صحيحة");
            }
            staffPermissionList.add(perm);
        }

        log.info("Permissions list\n{}", staffPermissionList);

        Staff staff = Staff.builder()
                .name(staffRequest.getName())
                .email(cleanEmail)
                .hash(HashUtils.sha256(password))
                .classes(staffRequest.getClasses())
                .build();


        staffPermissionList.forEach(perm -> perm.setStaff(staff));
        staff.setPermissions(staffPermissionList);
        staffRepository.save(staff);

        log.info("Staff saved to DB successfully:\n{}", staff);


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
            throw new UnhandledRejection("حدث خطأ أثناء إرسال البريد الإلكتروني");
        }

        log.info("Registration successful");
        log.info("[StaffService].[register] executed successfully");

        return new Response("تم التسجيل بنجاح");
    }

    @Transactional
    public Response changeEmail(ChangeEmail newEmailReq) {
        log.info("executing method [StaffService].[changeEmail]");

        log.info("new email body:\n{}", newEmailReq);

        if (newEmailReq.getEmail() == null || newEmailReq.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(newEmailReq.getEmail())) {
            log.error("Invalid email:\n{}", newEmailReq.getEmail());
            throw new UnhandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        if (staffRepository.existsByEmail(newEmailReq.getEmail())) {
            throw new UnhandledRejection("البريد الإلكتروني مستخدم بالفعل");
        }

        Staff staff = staffRepository.findById(getStaffId())
                .orElseThrow(() -> {
                    log.error("Staff not found");
                    return new UnhandledRejection("يرجى التأكد من البيانات");
                });

        String cleanEmail = newEmailReq.getEmail().trim();

        log.info("old staff object:\n{}", staff);
        staff.setEmail(cleanEmail);
        log.info("new staff object:\n{}", staff);
        staffRepository.save(staff);
        log.info("Email changed successfully")
        ;
        log.info("[StaffService].[changeEmail] executed successfully");
        return new Response("تم تغيير البريد الإلكتروني بنجاح");
    }

    public Response login(Login login, HttpSession session) {

        log.info("executing method [StaffService].[login]");

        Staff staff = staffRepository.findByEmail(login.getUsername())
                .orElseThrow(() -> {
                    log.error("Staff not found");
                    return new UnhandledRejection("يرجى التأكد من البيانات");
                });

        log.info("Login info\n{}", login);
        log.info("Staff info\n{}", staff);

        if (login.getUsername() == null || login.getPassword() == null ||
                login.getUsername().isBlank() || login.getPassword().isBlank() ||
                !staff.getHash().equals(HashUtils.sha256(login.getPassword()))) {
            log.error("Invalid login attempt");
            throw new UnhandledRejection("الرجاء إدخال اسم المستخدم وكلمة المرور");
        } else {
            StaffDetails staffDetails = new StaffDetails(staff);
            Authentication authentication = new UsernamePasswordAuthenticationToken(staffDetails, null, staffDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            log.info("Staff ID: {} logged in", staff.getId());
        }
        log.info("[StaffService].[login] executed successfully");

        return new Response("تم تسجيل الدخول بنجاح");
    }

}
