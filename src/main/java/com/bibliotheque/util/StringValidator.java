// Validation 
package com.bibliotheque.util;

public class StringValidator {
    
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public static boolean isValidISBN(String isbn) {
        return isbn != null && isbn.matches("^[0-9-]{10,17}$");
    }
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}