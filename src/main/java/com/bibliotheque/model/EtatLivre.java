package model;

/**
 * Enumération des différents états possibles d'un livre.
 */
public enum EtatLivre {
    
    DISPONIBLE("Disponible", "✅", "Le livre est disponible pour l'emprunt"),
    EMPRUNTE("Emprunté", "📚", "Le livre est actuellement emprunté"),
    RESERVE("Réservé", "⏳", "Le livre est réservé par un lecteur"),
    HORS_SERVICE("Hors service", "🔧", "Le livre est en réparation ou perdu"),
    EPUISE("Épuisé", "❌", "Aucun exemplaire disponible");
    
    private final String libelle;
    private final String icone;
    private final String description;
    
    EtatLivre(String libelle, String icone, String description) {
        this.libelle = libelle;
        this.icone = icone;
        this.description = description;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getIcone() {
        return icone;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Détermine l'état d'un livre basé sur sa quantité disponible
     */
    public static EtatLivre determinerEtat(int quantiteDisponible, int quantiteTotale) {
        if (quantiteTotale == 0) {
            return HORS_SERVICE;
        } else if (quantiteDisponible <= 0) {
            return EPUISE;
        } else if (quantiteDisponible > 0) {
            return DISPONIBLE;
        } else {
            return HORS_SERVICE;
        }
    }
    
    /**
     * Détermine l'état basé sur un booléen de disponibilité
     */
    public static EtatLivre determinerEtat(boolean disponible) {
        return disponible ? DISPONIBLE : EMPRUNTE;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", icone, libelle);
    }
}
