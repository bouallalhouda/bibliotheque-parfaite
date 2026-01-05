// Exception livre indisponible - pour signaler qu'un livre n'est pas disponible (déjà emprunté ou inexistant)
package com.bibliotheque.exception;

public class LivreIndisponibleException extends Exception {
    public LivreIndisponibleException(String message) {
        super(message);
    }
    
    public LivreIndisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
