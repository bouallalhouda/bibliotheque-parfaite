// Exception validation pour signaler des erreurs de saisie (email invalide, ISBN mal formé)
package com.bibliotheque.exception;
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
