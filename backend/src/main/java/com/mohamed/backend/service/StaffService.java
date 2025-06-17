package com.mohamed.backend.service;

import com.mohamed.backend.Utils.HashUtils;
import com.mohamed.backend.Utils.RandomNumberGenerator;
import com.mohamed.backend.Utils.SimpleEmail;
import com.mohamed.backend.Utils.ValidationUtils;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Class;
import com.mohamed.backend.model.Staff;
import com.mohamed.backend.model.StaffPermission;
import com.mohamed.backend.repository.ClassRepository;
import com.mohamed.backend.repository.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        Staff staff = Staff.builder()
                .name(staffRequest.getName())
                .email(staffRequest.getEmail())
                .hash(HashUtils.sha256(password))
                .classes(staffRequest.getClasses())
                .permissions(staffRequest.getPermissions())
                .build();

        staff = staffRepository.save(staff);

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
}
