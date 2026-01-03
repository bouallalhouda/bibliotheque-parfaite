package com.bibliotheque.dao;



import com.bibliotheque.model.Livre;
import java.util.List;

/**
 * Interface pour les opérations CRUD sur les livres
 */
public interface LivreDAO {
    
    // === CRUD BASIQUE ===
    Livre findById(int id);
    List<Livre> findAll();
    int save(Livre livre);
    boolean update(Livre livre);
    boolean delete(int id);
    
    // === RECHERCHES SIMPLES ===
    List<Livre> findByTitre(String titre);
    List<Livre> findByAuteur(String auteur);
    List<Livre> findByIsbn(String isbn);
    List<Livre> findByCategorie(String categorie);
    List<Livre> findByAnnee(int annee);
    
    // === RECHERCHES COMBINÉES ===
    List<Livre> searchGlobal(String motCle);
    List<Livre> searchAdvanced(String titre, String auteur, 
                              String categorie, String isbn,
                              Integer anneeMin, Integer anneeMax);
    
    // === STATISTIQUES ===
    int countTotalLivres();
    int countLivresDisponibles();
    int countLivresEmpruntes();
    List<String> findAllCategories();
    
    // === GESTION QUANTITÉS ===
    boolean updateQuantite(int idLivre, int nouvelleQuantite);
    boolean incrementerQuantiteDisponible(int idLivre);
    boolean decrementerQuantiteDisponible(int idLivre);
    
    // === VALIDATION ===
    boolean isbnExists(String isbn);
    boolean isbnExistsForOtherBook(String isbn, int idLivre);
}
