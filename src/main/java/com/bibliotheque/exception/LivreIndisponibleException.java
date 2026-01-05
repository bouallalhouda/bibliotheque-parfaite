
package com.bibliotheque.exception;

public class LimiteEmpruntDepasseeException extends Exception {
    public LimiteEmpruntDepasseeException(String message) {
        super(message);
    }
    
    public LimiteEmpruntDepasseeException(String message, Throwable cause) {
        super(message, cause);
    }
}
