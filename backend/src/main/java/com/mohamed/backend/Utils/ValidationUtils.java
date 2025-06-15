package com.mohamed.backend.Utils;

public class ValidationUtils {
    public static boolean isArabic(String text) {
        return text != null && text.matches("^[\\p{InArabic}\\s]+$");
    }

    public static boolean isValidCpr(Integer cpr) {
        return cpr != null && String.valueOf(cpr).matches("^\\d{9}$");
    }
}
