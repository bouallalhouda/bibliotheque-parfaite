// Exception limite emprunt 
package com.bibliotheque.exception;

public class LimiteEmpruntDepasseeException extends Exception {
    public LimiteEmpruntDepasseeException(String message) {
        super(message);
    }
}