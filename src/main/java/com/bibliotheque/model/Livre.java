
package com.bibliotheque.model;

import java.time.LocalDate;

/**
 * Classe représentant un Livre dans la bibliothèque.
 * Hérite de Document et implémente Empruntable.
 */
public class Livre extends Document {
    
    // === ATTRIBUTS SPÉCIFIQUES AU LIVRE ===
    private String isbn;
    private String editeur;
    private int nombrePages;
    private String langue;
    private int quantiteTotale;
    private int quantiteDisponible;
    
    // === CONSTRUCTEURS ===
    
    /**
     * Constructeur par défaut
     */
    public Livre() {
        super();
        this.langue = "Français";
        this.quantiteTotale = 1;
        this.quantiteDisponible = 1;
    }
    
    /**
     * Constructeur avec paramètres
     */
    public Livre(int id, String titre, String auteur, 
                LocalDate datePublication, String categorie,
                String isbn, String editeur, int nombrePages, 
                String langue, int quantiteTotale) {
        super(id, titre, auteur, datePublication, categorie);
        this.isbn = isbn;
        this.editeur = editeur;
        this.nombrePages = nombrePages;
        this.langue = langue;
        this.quantiteTotale = quantiteTotale;
        this.quantiteDisponible = quantiteTotale; // Initialement tous disponibles
        super.setDisponible(quantiteDisponible > 0);
    }
    
    // === IMPLÉMENTATION DES MÉTHODES ABSTRAITES (Document) ===
    
    @Override
    public String getTypeDocument() {
        return "Livre";
    }
    
    @Override
    public String getDescriptionDetaillee() {
        return String.format("Livre: %s\n" +
                           "Auteur: %s\n" +
                           "ISBN: %s\n" +
                           "Éditeur: %s\n" +
                           "Pages: %d\n" +
                           "Langue: %s\n" +
                           "Quantité: %d/%d",
                           getTitre(), getAuteur(), isbn, editeur,
                           nombrePages, langue, quantiteDisponible, quantiteTotale);
    }
    
    @Override
    public int getDureeMaxEmprunt() {
        return 21; // 3 semaines pour un livre
    }
    
    // === IMPLÉMENTATION DES MÉTHODES (Empruntable) ===
    
    @Override
    public boolean estDisponible() {
        return quantiteDisponible > 0;
    }
    
    @Override
    public String getTypeEmpruntable() {
        return getTypeDocument();
    }
    
    @Override
    public String getConditionsEmprunt() {
        return String.format("Durée max: %d jours. À retourner avant la date d'échéance.",
                            getDureeMaxEmprunt());
    }
    
    @Override
    public void emprunter() {
        if (quantiteDisponible <= 0) {
            throw new IllegalStateException("Aucun exemplaire disponible pour l'emprunt");
        }
        this.quantiteDisponible--;
        super.setDisponible(quantiteDisponible > 0);
    }
    
    @Override
    public void retourner() {
        if (quantiteDisponible >= quantiteTotale) {
            throw new IllegalStateException("Tous les exemplaires sont déjà disponibles");
        }
        this.quantiteDisponible++;
        super.setDisponible(true);
    }
    
    // === GETTERS ET SETTERS ===
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ISBN ne peut pas être vide");
        }
        this.isbn = isbn.trim();
    }
    
    public String getEditeur() {
        return editeur;
    }
    
    public void setEditeur(String editeur) {
        this.editeur = editeur != null ? editeur.trim() : "";
    }
    
    public int getNombrePages() {
        return nombrePages;
    }
    
    public void setNombrePages(int nombrePages) {
        if (nombrePages <= 0) {
            throw new IllegalArgumentException("Le nombre de pages doit être positif");
        }
        this.nombrePages = nombrePages;
    }
    
    public String getLangue() {
        return langue;
    }
    
    public void setLangue(String langue) {
        this.langue = langue != null ? langue.trim() : "Français";
    }
    
    public int getQuantiteTotale() {
        return quantiteTotale;
    }
    
    public void setQuantiteTotale(int quantiteTotale) {
        if (quantiteTotale < 0) {
            throw new IllegalArgumentException("La quantité totale ne peut pas être négative");
        }
        this.quantiteTotale = quantiteTotale;
        // Ajuster la quantité disponible si nécessaire
        if (this.quantiteDisponible > quantiteTotale) {
            this.quantiteDisponible = quantiteTotale;
        }
        super.setDisponible(this.quantiteDisponible > 0);
    }
    
    public int getQuantiteDisponible() {
        return quantiteDisponible;
    }
    
    public void setQuantiteDisponible(int quantiteDisponible) {
        if (quantiteDisponible < 0 || quantiteDisponible > quantiteTotale) {
            throw new IllegalArgumentException("Quantité disponible invalide. Doit être entre 0 et " + quantiteTotale);
        }
        this.quantiteDisponible = quantiteDisponible;
        super.setDisponible(quantiteDisponible > 0);
    }
    
    // === MÉTHODES MÉTIER ===
    
    /**
     * Ajoute des exemplaires au livre
     * @param quantite Nombre d'exemplaires à ajouter
     */
    public void ajouterExemplaires(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité à ajouter doit être positive");
        }
        this.quantiteTotale += quantite;
        this.quantiteDisponible += quantite;
        super.setDisponible(true);
    }
    
    /**
     * Retire des exemplaires (si pas empruntés)
     * @param quantite Nombre d'exemplaires à retirer
     */
    public void retirerExemplaires(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité à retirer doit être positive");
        }
        
        int exemplairesEmpruntes = quantiteTotale - quantiteDisponible;
        if (quantite > quantiteDisponible) {
            throw new IllegalStateException("Impossible de retirer " + quantite + 
                                          " exemplaires. Seulement " + quantiteDisponible + 
                                          " sont disponibles (et " + exemplairesEmpruntes + 
                                          " sont empruntés)");
        }
        
        this.quantiteTotale -= quantite;
        this.quantiteDisponible -= quantite;
        super.setDisponible(quantiteDisponible > 0);
    }
    
    /**
     * Vérifie si des exemplaires sont actuellement empruntés
     */
    public boolean aDesExemplairesEmpruntes() {
        return quantiteDisponible < quantiteTotale;
    }
    
    /**
     * Retourne le nombre d'exemplaires empruntés
     */
    public int getNombreExemplairesEmpruntes() {
        return quantiteTotale - quantiteDisponible;
    }
    
    // === MÉTHODES D'AFFICHAGE ET COMPARAISON ===
    
    @Override
    public String toString() {
        return String.format("📖 %s [ID: %d] - %s par %s - %d/%d exemplaires - %s",
                getTypeDocument(),
                getId(),
                getTitre(),
                getAuteur(),
                quantiteDisponible,
                quantiteTotale,
                estDisponible() ? "✅ Disponible" : "❌ Épuisé");
    }
    
    /**
     * Format détaillé pour l'affichage dans les listes
     */
    public String toStringDetaille() {
        return String.format(
            "ID: %d\n" +
            "Titre: %s\n" +
            "Auteur: %s\n" +
            "ISBN: %s\n" +
            "Année: %d\n" +
            "Catégorie: %s\n" +
            "Éditeur: %s\n" +
            "Pages: %d\n" +
            "Langue: %s\n" +
            "Exemplaires: %d/%d\n" +
            "État: %s",
            getId(),
            getTitre(),
            getAuteur(),
            isbn,
            getDatePublication().getYear(),
            getCategorie(),
            editeur,
            nombrePages,
            langue,
            quantiteDisponible,
            quantiteTotale,
            EtatLivre.determinerEtat(quantiteDisponible, quantiteTotale)
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        
        Livre livre = (Livre) obj;
        
        // Deux livres sont égaux s'ils ont le même ISBN
        return isbn != null ? isbn.equals(livre.isbn) : livre.isbn == null;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        return result;
    }
    
    // === MÉTHODES DE RECHERCHE ===
    
    @Override
    public boolean correspondRecherche(String motCle) {
        boolean correspondSuper = super.correspondRecherche(motCle);
        if (correspondSuper) {
            return true;
        }
        
        if (motCle == null || motCle.trim().isEmpty()) {
            return true;
        }
        
        String recherche = motCle.toLowerCase().trim();
        return (isbn != null && isbn.toLowerCase().contains(recherche)) ||
               (editeur != null && editeur.toLowerCase().contains(recherche)) ||
               (langue != null && langue.toLowerCase().contains(recherche));
    }
    
    /**
     * Vérifie si le livre correspond à une recherche avancée
     */
    public boolean correspondRechercheAvancee(String titre, String auteur, 
                                             String categorie, String isbnRecherche,
                                             Integer anneeMin, Integer anneeMax) {
        boolean correspond = true;
        
        if (titre != null && !titre.isEmpty()) {
            correspond = correspond && (getTitre() != null && 
                         getTitre().toLowerCase().contains(titre.toLowerCase()));
        }
        
        if (auteur != null && !auteur.isEmpty()) {
            correspond = correspond && (getAuteur() != null && 
                         getAuteur().toLowerCase().contains(auteur.toLowerCase()));
        }
        
        if (categorie != null && !categorie.isEmpty()) {
            correspond = correspond && (getCategorie() != null && 
                         getCategorie().equalsIgnoreCase(categorie));
        }
        
        if (isbnRecherche != null && !isbnRecherche.isEmpty()) {
            correspond = correspond && (isbn != null && 
                         isbn.equalsIgnoreCase(isbnRecherche));
        }
        
        if (anneeMin != null) {
            correspond = correspond && (getDatePublication().getYear() >= anneeMin);
        }
        
        if (anneeMax != null) {
            correspond = correspond && (getDatePublication().getYear() <= anneeMax);
        }
        
        return correspond;
    }
    
    // === MÉTHODES DE VALIDATION ===
    
    /**
     * Valide toutes les données du livre
     */
    public boolean valider() {
        try {
            // Validation des attributs hérités
            if (getTitre() == null || getTitre().trim().isEmpty()) {
                return false;
            }
            
            if (getAuteur() == null || getAuteur().trim().isEmpty()) {
                return false;
            }
            
            if (getDatePublication() == null) {
                return false;
            }
            
            // Validation des attributs spécifiques
            if (isbn == null || isbn.trim().isEmpty()) {
                return false;
            }
            
            if (nombrePages <= 0) {
                return false;
            }
            
            if (quantiteTotale < 0) {
                return false;
            }
            
            if (quantiteDisponible < 0 || quantiteDisponible > quantiteTotale) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}
