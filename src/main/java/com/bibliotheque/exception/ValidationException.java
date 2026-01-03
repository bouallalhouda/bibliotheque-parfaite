// Exception validation 
package com.bibliotheque.exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}