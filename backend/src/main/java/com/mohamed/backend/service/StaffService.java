package com.mohamed.backend.service;

import com.mohamed.backend.model.StaffPermission;
import com.mohamed.backend.utils.HashUtils;
import com.mohamed.backend.utils.RandomNumberGenerator;
import com.mohamed.backend.utils.SimpleEmail;
import com.mohamed.backend.utils.ValidationUtils;
import com.mohamed.backend.dto.ChangeEmail;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Class;
import com.mohamed.backend.model.Staff;
import com.mohamed.backend.repository.ClassRepository;
import com.mohamed.backend.repository.StaffRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Integer getStaffIdFromSession(HttpSession session) {
        return (Integer) session.getAttribute("staffId");
    }


    public Page<Staff> getStaff(Pageable pageable){
        return staffRepository.findAll(pageable);
    }

    @Transactional
    public Response register(Staff staffRequest){
        log.info("Staff info: {}", staffRequest);

        if (staffRequest.getName() == null || staffRequest.getName().trim().isEmpty() || !ValidationUtils.isArabic(staffRequest.getName())) {
            log.error("Invalid name: {}", staffRequest.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (staffRequest.getEmail() == null || staffRequest.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(staffRequest.getEmail())) {
            log.error("Invalid email: {}", staffRequest.getEmail());
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

        log.info("Permissions list {}", staffPermissionList);

        Staff staff = Staff.builder()
                .name(staffRequest.getName())
                .email(cleanEmail)
                .hash(HashUtils.sha256(password))
                .classes(staffRequest.getClasses())
                .build();


        staffPermissionList.forEach(perm -> perm.setStaff(staff));
        staff.setPermissions(staffPermissionList);
        staffRepository.save(staff);

        log.info("Staff saved to DB successfully: {}", staff);


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
            log.error("Failed to send email to {}", to);
            log.error(e.getMessage());
            throw new UnhandledRejection("حدث خطأ أثناء إرسال البريد الإلكتروني");
        }

        log.info("Registration successful");

        return new Response("تم التسجيل بنجاح");
    }

    @Transactional
    public Response changeEmail(ChangeEmail newEmailReq, HttpSession httpSession) {
        log.info("new email body: {}", newEmailReq);

        if (newEmailReq.getEmail() == null || newEmailReq.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(newEmailReq.getEmail())) {
            log.error("Invalid email: {}", newEmailReq.getEmail());
            throw new UnhandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        if (staffRepository.existsByEmail(newEmailReq.getEmail())) {
            throw new UnhandledRejection("البريد الإلكتروني مستخدم بالفعل");
        }

        Staff staff = staffRepository.findById(getStaffIdFromSession(httpSession))
                .orElseThrow(() -> new UnhandledRejection("يرجى التأكد من البيانات"));

        String cleanEmail = newEmailReq.getEmail().trim();

        log.info("old staff object: {}", staff);
        staff.setEmail(cleanEmail);
        log.info("new staff object: {}", staff);
        staffRepository.save(staff);
        log.info("Email changed successfully");
        return new Response("تم تغيير البريد الإلكتروني بنجاح");
    }
}
