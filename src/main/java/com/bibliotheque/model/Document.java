package com.bibliotheque.model;


import java.time.LocalDate;

/**
 * Classe abstraite représentant un document dans la bibliothèque.
 * C'est la classe parente de tous les types de documents (Livre, Revue, DVD, etc.).
 * Implémente l'interface Empruntable.
 */
public abstract class Document implements Empruntable {
    
    // === ATTRIBUTS COMMUNS À TOUS LES DOCUMENTS ===
    private int id;
    private String titre;
    private String auteur;
    private LocalDate datePublication;
    private String categorie;
    private boolean disponible;
    
    // === CONSTRUCTEURS ===
    
    /**
     * Constructeur par défaut
     */
    public Document() {
        this.disponible = true; // Par défaut, un document est disponible
    }
    
    /**
     * Constructeur avec paramètres
     */
    public Document(int id, String titre, String auteur, 
                   LocalDate datePublication, String categorie) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.datePublication = datePublication;
        this.categorie = categorie;
        this.disponible = true;
    }
    
    // === MÉTHODES ABSTRAITES (À IMPLÉMENTER PAR LES SOUS-CLASSES) ===
    
    /**
     * Retourne le type de document (Livre, Revue, etc.)
     */
    public abstract String getTypeDocument();
    
    /**
     * Retourne une description détaillée du document
     */
    public abstract String getDescriptionDetaillee();
    
    // === IMPLÉMENTATION DE L'INTERFACE EMPRUNTABLE ===
    
    @Override
    public boolean estDisponible() {
        return this.disponible;
    }
    
    @Override
    public String getTypeEmpruntable() {
        return getTypeDocument();
    }
    
    @Override
    public String getConditionsEmprunt() {
        return "À retourner avant la date d'échéance.";
    }
    
    @Override
    public void emprunter() {
        if (!this.disponible) {
            throw new IllegalStateException("Le document n'est pas disponible pour l'emprunt");
        }
        this.disponible = false;
    }
    
    @Override
    public void retourner() {
        this.disponible = true;
    }
    
    @Override
    public abstract int getDureeMaxEmprunt();
    
    // === GETTERS ET SETTERS ===
    
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
        // Vérifier que la date n'est pas trop dans le futur
        if (datePublication.isAfter(LocalDate.now().plusYears(5))) {
            throw new IllegalArgumentException("La date de publication ne peut pas être dans plus de 5 ans");
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
    
    // === MÉTHODES MÉTIER ===
    
    /**
     * Vérifie si le document peut être emprunté
     */
    public boolean peutEtreEmprunte() {
        return this.disponible;
    }
    
    /**
     * Vérifie si le document est en retard (à utiliser avec la date d'emprunt)
     */
    public boolean estEnRetard(LocalDate dateEmprunt) {
        LocalDate dateRetourPrevue = dateEmprunt.plusDays(getDureeMaxEmprunt());
        return LocalDate.now().isAfter(dateRetourPrevue);
    }
    
    /**
     * Calcule le nombre de jours de retard
     */
    public int calculerJoursRetard(LocalDate dateEmprunt) {
        LocalDate dateRetourPrevue = dateEmprunt.plusDays(getDureeMaxEmprunt());
        if (LocalDate.now().isAfter(dateRetourPrevue)) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
        }
        return 0;
    }
    
    // === MÉTHODES D'AFFICHAGE ===
    
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
    
    /**
     * Format court pour les listes
     */
    public String toStringCourt() {
        return String.format("%s - %s", titre, auteur);
    }
    
    // === MÉTHODES DE COMPARAISON ===
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Document document = (Document) obj;
        return id == document.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    /**
     * Compare par titre (pour le tri)
     */
    public int compareParTitre(Document autre) {
        return this.titre.compareToIgnoreCase(autre.titre);
    }
    
    /**
     * Compare par auteur (pour le tri)
     */
    public int compareParAuteur(Document autre) {
        return this.auteur.compareToIgnoreCase(autre.auteur);
    }
    
    /**
     * Compare par date (pour le tri)
     */
    public int compareParDate(Document autre) {
        return this.datePublication.compareTo(autre.datePublication);
    }
    
    // === MÉTHODES DE RECHERCHE ===
    
    /**
     * Vérifie si le document correspond à une recherche par mot-clé
     */
    public boolean correspondRecherche(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return true;
        }
        
        String recherche = motCle.toLowerCase().trim();
        return (titre != null && titre.toLowerCase().contains(recherche)) ||
               (auteur != null && auteur.toLowerCase().contains(recherche)) ||
               (categorie != null && categorie.toLowerCase().contains(recherche));
    }
    
    /**
     * Vérifie si le document correspond à une recherche avancée
     */
    public boolean correspondRechercheAvancee(String titreRecherche, String auteurRecherche, 
                                             String categorieRecherche, Integer anneeMin, Integer anneeMax) {
        boolean correspond = true;
        
        if (titreRecherche != null && !titreRecherche.isEmpty()) {
            correspond = correspond && (titre != null && 
                         titre.toLowerCase().contains(titreRecherche.toLowerCase()));
        }
        
        if (auteurRecherche != null && !auteurRecherche.isEmpty()) {
            correspond = correspond && (auteur != null && 
                         auteur.toLowerCase().contains(auteurRecherche.toLowerCase()));
        }
        
        if (categorieRecherche != null && !categorieRecherche.isEmpty()) {
            correspond = correspond && (categorie != null && 
                         categorie.equalsIgnoreCase(categorieRecherche));
        }
        
        if (anneeMin != null) {
            correspond = correspond && (datePublication.getYear() >= anneeMin);
        }
        
        if (anneeMax != null) {
            correspond = correspond && (datePublication.getYear() <= anneeMax);
        }
        
        return correspond;
    }
    
    // === MÉTHODES DE VALIDATION ===
    
    /**
     * Valide toutes les données du document
     */
    public boolean valider() {
        try {
            if (titre == null || titre.trim().isEmpty()) {
                return false;
            }
            
            if (auteur == null || auteur.trim().isEmpty()) {
                return false;
            }
            
            if (datePublication == null) {
                return false;
            }
            
            if (datePublication.isAfter(LocalDate.now().plusYears(5))) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Vérifie si le document est valide pour l'emprunt
     */
    public boolean estValidePourEmprunt() {
        return valider() && disponible;
    }
    
    // === MÉTHODES STATIQUES UTILITAIRES ===
    
    /**
     * Génère un ID temporaire (pour les nouveaux documents non sauvegardés)
     */
    public static int genererIdTemporaire() {
        return -(int) (Math.random() * 1000000);
    }
    
    /**
     * Vérifie si un ID est temporaire (négatif)
     */
    public static boolean estIdTemporaire(int id) {
        return id < 0;
    }
}
