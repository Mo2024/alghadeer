package com.mohamed.backend.utils;

import com.mohamed.backend.model.SemesterList;

import java.time.LocalDate;
import java.time.Year;

public class ValidationUtils {
    public static boolean isArabic(String text) {
        return text != null && text.matches("^[\\p{InArabic}\\s]+$");
    }

    public static boolean isValidCpr(String cpr) {
        return cpr != null && String.valueOf(cpr).matches("^\\d{9}$");
    }

    public static boolean isValidTelephone(String telephone) {
        return telephone != null && String.valueOf(telephone).matches("^\\d{8}$");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    public static boolean isValidSemester(String value) {
        for (SemesterList s : SemesterList.values()) {
            if (s.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidYear(Integer year) {
        if (year == null) return false;
        int currentYear = Year.now().getValue();
        return year >= currentYear;
    }


}
