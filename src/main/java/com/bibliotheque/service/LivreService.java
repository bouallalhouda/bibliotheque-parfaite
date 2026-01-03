package com.bibliotheque.service;

import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.impl.LivreDAOImpl;
import com.bibliotheque.model.Livre;
import java.util.List;

/**
 * Service pour la gestion des livres (couche métier)
 */
public class LivreService {
    
    private LivreDAO livreDAO;
    
    public LivreService() {
        this.livreDAO = new LivreDAOImpl();
    }
    
    // === GESTION LIVRES ===
    public List<Livre> getAllLivres() {
        return livreDAO.findAll();
    }
    
    public Livre getLivreById(int id) {
        return livreDAO.findById(id);
    }
    
    public boolean ajouterLivre(Livre livre) {
        // Validation
        if (!validerLivre(livre)) {
            return false;
        }
        
        // Vérifier ISBN unique
        if (livreDAO.isbnExists(livre.getIsbn())) {
            throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà");
        }
        
        // Sauvegarder
        int id = livreDAO.save(livre);
        if (id > 0) {
            livre.setId(id);
            return true;
        }
        return false;
    }
    
    public boolean modifierLivre(Livre livre) {
        // Validation
        if (!validerLivre(livre)) {
            return false;
        }
        
        // Vérifier ISBN unique pour un autre livre
        if (livreDAO.isbnExistsForOtherBook(livre.getIsbn(), livre.getId())) {
            throw new IllegalArgumentException("Cet ISBN est déjà utilisé par un autre livre");
        }
        
        return livreDAO.update(livre);
    }
    
    public boolean supprimerLivre(int id) {
        // Vérifier si le livre peut être supprimé
        Livre livre = livreDAO.findById(id);
        if (livre != null && livre.getQuantiteDisponible() < livre.getQuantiteTotale()) {
            throw new IllegalStateException("Impossible de supprimer : des exemplaires sont empruntés");
        }
        
        return livreDAO.delete(id);
    }
    
    // === RECHERCHE ===
    public List<Livre> rechercherParTitre(String titre) {
        return livreDAO.findByTitre(titre);
    }
    
    public List<Livre> rechercherParAuteur(String auteur) {
        return livreDAO.findByAuteur(auteur);
    }
    
    public List<Livre> rechercherParId(int id) {
        Livre livre = livreDAO.findById(id);
        return livre != null ? List.of(livre) : List.of();
    }
    
    public List<Livre> rechercherGlobal(String motCle) {
        return livreDAO.searchGlobal(motCle);
    }
    
    public List<Livre> rechercherAvancee(String titre, String auteur, 
                                        String categorie, String isbn,
                                        Integer anneeMin, Integer anneeMax) {
        return livreDAO.searchAdvanced(titre, auteur, categorie, isbn, anneeMin, anneeMax);
    }
    
    // === STATISTIQUES ===
    public int getNombreTotalLivres() {
        return livreDAO.countTotalLivres();
    }
    
    public int getNombreLivresDisponibles() {
        return livreDAO.countLivresDisponibles();
    }
    
    public List<String> getCategories() {
        return livreDAO.findAllCategories();
    }
    
    // === VALIDATION ===
    private boolean validerLivre(Livre livre) {
        if (livre == null) {
            throw new IllegalArgumentException("Le livre ne peut pas être null");
        }
        
        if (livre.getTitre() == null || livre.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        
        if (livre.getAuteur() == null || livre.getAuteur().trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur est obligatoire");
        }
        
        if (livre.getIsbn() == null || livre.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ISBN est obligatoire");
        }
        
        if (livre.getQuantiteTotale() < 0) {
            throw new IllegalArgumentException("La quantité totale doit être positive");
        }
        
        if (livre.getQuantiteDisponible() < 0 || 
            livre.getQuantiteDisponible() > livre.getQuantiteTotale()) {
            throw new IllegalArgumentException("Quantité disponible invalide");
        }
        
        return true;
    }
    
    // === GESTION QUANTITÉS ===
    public boolean ajouterExemplaires(int idLivre, int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        
        Livre livre = livreDAO.findById(idLivre);
        if (livre == null) {
            throw new IllegalArgumentException("Livre non trouvé");
        }
        
        livre.setQuantiteTotale(livre.getQuantiteTotale() + quantite);
        livre.setQuantiteDisponible(livre.getQuantiteDisponible() + quantite);
        
        return livreDAO.update(livre);
    }
}
