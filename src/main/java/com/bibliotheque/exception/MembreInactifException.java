// Exception membre inactif - pour signaler qu'un membre est désactivé et ne peut pas emprunter
package com.bibliotheque.exception;

public class MembreInactifException extends Exception {
    public MembreInactifException(String message) {
        super(message);
    }
    
    public MembreInactifException(String message, Throwable cause) {
        super(message, cause);
    }
}
