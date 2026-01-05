package com.bibliotheque.util;

public class StringValidator {
    // Valide un email basique
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String trimmedEmail = email.trim();
        return trimmedEmail.contains("@") && 
               trimmedEmail.contains(".") && 
               trimmedEmail.indexOf("@") < trimmedEmail.lastIndexOf(".");
    }
    
    // Valide un ISBN (10 ou 13 chiffres)
    public static boolean isValidIsbn(String isbn) {
        if (isbn == null) {
            return false;
        }
        // Enlève tous les tirets
        String cleanIsbn = isbn.replaceAll("-", "");
        // Vérifie 10 ou 13 chiffres exactement
        return cleanIsbn.matches("\\d{10}|\\d{13}");
    }
    
    // Vérifie si une chaîne n'est pas vide
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    // Valide un nom/prénom (lettres, espaces, tirets)
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches("[a-zA-Z\\s\\-]+");
    }
}
