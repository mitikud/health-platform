package com.medical.medicationservice.util;

public class Lang {
    public static String normalize(String lang) {
        if (lang == null) return "en";
        return switch (lang.toLowerCase()) {
            case "am", "amh", "am-et" -> "am";
            case "ti", "tir", "ti-et" -> "ti";
            default -> "en";
        };
    }
}
