package com.example.library.common;

public class Regex {
    public static final String INTEGER_NUMBER = "^[1-9]\\d*$"; // 112312312312 // -123/ abcsdf
    public static final String EMAIL = "[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PHONE_NUMBER = "^(0[1-9]|84[1-9])[0-9]{8}$";

    public static boolean isValid( String regex, String value){
        return switch (regex) {
            case "INTEGER_NUMBER" -> value.matches(INTEGER_NUMBER);
            case "EMAIL" -> value.matches(EMAIL);
            case "PHONE_NUMBER" -> value.matches(PHONE_NUMBER);
            default -> false;
        };
    }

}
