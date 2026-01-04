// Exception membre inactif 
package com.bibliotheque.exception;

public class MembreInactifException extends Exception {
    public MembreInactifException(String message) {
        super(message);
    }
    
    public MembreInactifException(String message, Throwable cause) {
        super(message, cause);
    }
}
