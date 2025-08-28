package com.medical.diagnosisservice.util;

public class Lang {
    public static String normalize(String lang) {
        if (lang == null) return "en";
        return switch (lang.toLowerCase()) {
            case "am", "amh", "am-et" -> "am";
            case "ti", "tir", "ti-et" -> "ti";
            default -> "en";
        };
    }

    public static String locale(String lang) {
        return switch (normalize(lang)) {
            case "am" -> "am-ET";
            case "ti" -> "ti-ET";
            default -> "en-US";
        };
    }
}
