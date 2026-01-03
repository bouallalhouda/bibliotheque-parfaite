package com.bibliotheque.model;



import java.time.LocalDate;

/**
 * Classe abstraite représentant un document dans la bibliothèque.
 * C'est la classe parente de tous les types de documents (Livre, Revue, DVD, etc.).
 */
public abstract class Document {
    
    // Attributs communs à tous les documents
    private int id;
    private String titre;
    private String auteur;
    private LocalDate datePublication;
    private String categorie;
    private boolean disponible;
    
    // Constructeur par défaut
    public Document() {
        this.disponible = true; // Par défaut, un document est disponible
    }
    
    // Constructeur avec paramètres
    public Document(int id, String titre, String auteur, 
                   LocalDate datePublication, String categorie) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.datePublication = datePublication;
        this.categorie = categorie;
        this.disponible = true;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide");
        }
        this.titre = titre.trim();
    }
    
    public String getAuteur() {
        return auteur;
    }
    
    public void setAuteur(String auteur) {
        if (auteur == null || auteur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur ne peut pas être vide");
        }
        this.auteur = auteur.trim();
    }
    
    public LocalDate getDatePublication() {
        return datePublication;
    }
    
    public void setDatePublication(LocalDate datePublication) {
        if (datePublication == null) {
            throw new IllegalArgumentException("La date de publication ne peut pas être nulle");
        }
        if (datePublication.isAfter(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("La date de publication ne peut pas être dans le futur lointain");
        }
        this.datePublication = datePublication;
    }
    
    public String getCategorie() {
        return categorie;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie != null ? categorie.trim() : "";
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    // Méthodes abstraites que les sous-classes doivent implémenter
    /**
     * Retourne le type de document (Livre, Revue, etc.)
     */
    public abstract String getTypeDocument();
    
    /**
     * Retourne une description détaillée du document
     */
    public abstract String getDescriptionDetaillee();
    
    /**
     * Calcule la durée maximale d'emprunt pour ce type de document
     */
    public abstract int getDureeMaxEmprunt();
    
    // Méthodes concrètes communes
    /**
     * Vérifie si le document peut être emprunté
     */
    public boolean peutEtreEmprunte() {
        return this.disponible;
    }
    
    /**
     * Marque le document comme emprunté
     */
    public void emprunter() {
        if (!this.disponible) {
            throw new IllegalStateException("Le document n'est pas disponible pour l'emprunt");
        }
        this.disponible = false;
    }
    
    /**
     * Marque le document comme retourné
     */
    public void retourner() {
        this.disponible = true;
    }
    
    // Méthode toString() pour affichage
    @Override
    public String toString() {
        return String.format("%s [ID: %d] - %s par %s (%s) - %s",
                getTypeDocument(),
                id,
                titre,
                auteur,
                datePublication != null ? datePublication.getYear() : "N/A",
                disponible ? "✅ Disponible" : "❌ Emprunté");
    }
    
    // Méthode equals() basée sur l'ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Document document = (Document) obj;
        return id == document.id;
    }
    
    // Méthode hashCode() basée sur l'ID
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    // Méthode pour vérifier si le document correspond à une recherche
    public boolean correspondRecherche(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return true;
        }
        
        String recherche = motCle.toLowerCase().trim();
        return (titre != null && titre.toLowerCase().contains(recherche)) ||
               (auteur != null && auteur.toLowerCase().contains(recherche)) ||
               (categorie != null && categorie.toLowerCase().contains(recherche));
    }
}
