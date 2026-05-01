package com.nathannolacio.escala_do_reino_backend.core.util;

public class MaskUtils {

    private MaskUtils() {
        // Classe utilitária, não deve ser instanciada
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "---";
        }
        int atIndex = email.indexOf("@");
        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (name.length() <= 2) {
            return name + "***" + domain;
        }
        return name.substring(0, 2) + "***" + domain;
    }
}
