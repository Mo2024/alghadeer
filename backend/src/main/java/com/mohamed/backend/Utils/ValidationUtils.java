package com.mohamed.backend.Utils;

import java.time.LocalDate;

public class ValidationUtils {
    public static boolean isArabic(String text) {
        return text != null && text.matches("^[\\p{InArabic}\\s]+$");
    }

    public static boolean isValidCpr(Integer cpr) {
        return cpr != null && String.valueOf(cpr).matches("^\\d{9}$");
    }

    public static boolean isValidTelephone(Integer telephone) {
        return telephone != null && String.valueOf(telephone).matches("^\\d{8}$");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

}
