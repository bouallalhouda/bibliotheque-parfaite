package com.bibliotheque.model;


/**
 * Interface pour les objets qui peuvent être empruntés dans la bibliothèque.
 */
public interface Empruntable {
    
    /**
     * Vérifie si l'objet est disponible pour l'emprunt
     */
    boolean estDisponible();
    
    /**
     * Emprunte l'objet
     * @throws IllegalStateException si l'objet n'est pas disponible
     */
    void emprunter() throws IllegalStateException;
    
    /**
     * Retourne l'objet
     * @throws IllegalStateException si l'objet n'était pas emprunté
     */
    void retourner() throws IllegalStateException;
    
    /**
     * Retourne la durée maximale d'emprunt en jours
     */
    int getDureeMaxEmprunt();
    
    /**
     * Retourne le type d'objet empruntable
     */
    String getTypeEmpruntable();
    
    /**
     * Retourne les conditions d'emprunt spécifiques
     */
    String getConditionsEmprunt();
}
