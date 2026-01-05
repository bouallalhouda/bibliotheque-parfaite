package com.bibliotheque.service;

import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.impl.LivreDAOImpl;
import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.dao.MembreDAOImpl;
import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.dao.EmpruntDAOImpl;
import com.bibliotheque.exception.ValidationException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.MembreInactifException;
import com.bibliotheque.exception.LimiteEmpruntDepasseeException;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.util.StringValidator;
import com.bibliotheque.util.DateUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BibliothequeService {
    private final LivreDAO livreDAO;
    private final MembreDAO membreDAO;
    private final EmpruntDAO empruntDAO;

    public BibliothequeService() throws SQLException {
        this.livreDAO = new LivreDAOImpl();
        this.membreDAO = new MembreDAOImpl();
        this.empruntDAO = new EmpruntDAOImpl();
    }

    
    public void ajouterLivre(Livre livre) throws ValidationException {
        if (livre.getIsbn() == null || livre.getIsbn().trim().isEmpty()) {
            throw new ValidationException("L'ISBN ne peut pas être vide.");
        }
        // CORRECTION ICI : utiliser isValidIsbn() au lieu de validerISBN()
        if (!StringValidator.isValidIsbn(livre.getIsbn())) {
            throw new ValidationException("L'ISBN '" + livre.getIsbn() + "' n'est pas au bon format (10 ou 13 chiffres).");
        }
        livreDAO.save(livre);
        System.out.println(" Livre '" + livre.getTitre() + "' ajouté avec succès.");
    }

    
public void modifierLivre(String isbn, Livre nouveau) {
    // NE PAS utiliser nouveau.setId() car pas de colonne id
    livreDAO.update(nouveau);
    System.out.println(" Livre mis à jour avec succès.");
}


public void supprimerLivre(String isbn) {
    try {
        // SOLUTION DIRECTE SANS utiliser getId() 
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/bibliotheque", "root", "");
        String sql = "DELETE FROM livre WHERE isbn = ?";
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, isbn);
        int rowsDeleted = stmt.executeUpdate();
        stmt.close();
        conn.close();
        
        if (rowsDeleted > 0) {
            System.out.println(" Livre supprimé avec succès (ISBN: " + isbn + ")");
        } else {
            System.err.println(" Aucun livre trouvé avec l'ISBN: " + isbn);
        }
        
    } catch (Exception e) {
        System.err.println(" Erreur suppression livre: " + e.getMessage());
    }
}

    public List<Livre> rechercherLivres(String critere) {
        List<Livre> resultats = new ArrayList<>();
        
        // Recherche dans les titres
        resultats.addAll(livreDAO.findByTitre(critere));
        
        // Recherche dans les auteurs (éviter les doublons)
        List<Livre> parAuteur = livreDAO.findByAuteur(critere);
        for (Livre livre : parAuteur) {
            if (!resultats.contains(livre)) {
                resultats.add(livre);
            }
        }
        
        return resultats;
    }

    public List<Livre> getLivresDisponibles() {
        return livreDAO.findDisponibles();
    }

    
    public List<Livre> getTousLesLivres() {
        return livreDAO.findAll();
    }

    
     
    public void ajouterMembre(Membre membre) throws ValidationException {
        if (membre.getEmail() == null || membre.getEmail().trim().isEmpty()) {
            throw new ValidationException("L'email ne peut pas être vide.");
        }
        
        if (!StringValidator.isValidEmail(membre.getEmail())) {
            throw new ValidationException("L'email '" + membre.getEmail() + "' n'est pas au bon format.");
        }
        membreDAO.save(membre);
        System.out.println(" Membre '" + membre.getPrenom() + " " + membre.getNom() + "' ajouté avec succès.");
    }

    //Modifie un membre existant
     
    public void modifierMembre(int id, Membre nouveau) {
        Optional<Membre> optMembre = membreDAO.findById(id);
        if (optMembre.isPresent()) {
            nouveau.setId(id);
            membreDAO.update(nouveau);
            System.out.println("Membre mis à jour avec succès.");
        } else {
            System.err.println(" Aucun membre trouvé avec l'ID: " + id);
        }
    }

    //Active ou désactive un membre
     
    public void activerDesactiverMembre(int id) {
        Optional<Membre> optMembre = membreDAO.findById(id);
        if (optMembre.isPresent()) {
            Membre membre = optMembre.get();
            membre.setActif(!membre.isActif());
            membreDAO.update(membre);
            String statut = membre.isActif() ? "activé" : "désactivé";
            System.out.println(" Membre " + statut + " avec succès.");
        } else {
            System.err.println(" Aucun membre trouvé avec l'ID: " + id);
        }
    }

    // Retourne tous les membres
     
    public List<Membre> getTousLesMembres() {
        return membreDAO.findAll();
    }

    public List<Membre> getMembresActifs() {
        return membreDAO.findActifs();
    }

    //Recherche un membre par email
     
    public Membre rechercherMembreParEmail(String email) {
        return membreDAO.findByEmail(email);
    }

    
    public Emprunt emprunterLivre(String isbn, int idMembre) 
            throws LivreIndisponibleException, MembreInactifException, LimiteEmpruntDepasseeException {
        
        // 1. Vérifier si le livre existe et est disponible
        Livre livre = livreDAO.findByIsbn(isbn);
        if (livre == null) {
            throw new LivreIndisponibleException("Le livre avec l'ISBN '" + isbn + "' n'existe pas.");
        }
        if (!livre.peutEtreEmprunte()) {
            throw new LivreIndisponibleException("Le livre avec l'ISBN '" + isbn + "' n'est pas disponible.");
        }

        // 2. Vérifier si le membre existe et est actif
        Optional<Membre> optMembre = membreDAO.findById(idMembre);
        if (!optMembre.isPresent()) {
            throw new MembreInactifException("Le membre avec l'ID " + idMembre + " n'existe pas.");
        }
        Membre membre = optMembre.get();
        if (!membre.isActif()) {
            throw new MembreInactifException("Le membre '" + membre.getPrenom() + " " + membre.getNom() + "' est inactif et ne peut pas emprunter.");
        }

        // 3. Vérifier si le membre a moins de 3 emprunts en cours
        int nbEmpruntsEnCours = empruntDAO.countEmpruntsEnCours(idMembre);
        if (nbEmpruntsEnCours >= 3) {
            throw new LimiteEmpruntDepasseeException("Le membre a atteint la limite de 3 emprunts en cours.");
        }

        // 4. Créer l'emprunt
        Emprunt emprunt = new Emprunt();
        emprunt.setLivre(livre);
        emprunt.setMembre(membre);
        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetourPrevue(DateUtils.genererDateRetourPrevue());
        
        // Marquer le livre comme non disponible
        livre.emprunter();
        
        // Sauvegarder l'emprunt et mettre à jour le livre
        empruntDAO.save(emprunt);
        livreDAO.update(livre);
        
        System.out.println(" Emprunt créé: '" + livre.getTitre() + "' pour " + membre.getPrenom() + " " + membre.getNom());
        return emprunt;
    }

    
    public void retournerLivre(int idEmprunt) {
        Optional<Emprunt> optEmprunt = empruntDAO.findById(idEmprunt);
        if (optEmprunt.isPresent()) {
            Emprunt emprunt = optEmprunt.get();
            
            // Mettre à jour la date de retour effective
            emprunt.setDateRetourEffective(LocalDate.now());
            
            // Rendre le livre disponible
            Livre livre = emprunt.getLivre();
            livre.retourner();
            
            // Sauvegarder les modifications
            empruntDAO.update(emprunt);
            livreDAO.update(livre);
            
            System.out.println("Livre retourné avec succès: '" + livre.getTitre() + "'");
            
            // Affiche la pénalité si retard
            int joursRetard = DateUtils.calculerJoursRetard(
                emprunt.getDateRetourPrevue(),
                emprunt.getDateRetourEffective()
            );
            if (joursRetard > 0) {
                double penalite = DateUtils.calculerPenalite(joursRetard);
                System.out.println("  Retard de " + joursRetard + " jour(s). Pénalité: " + penalite + "€");
            }
        } else {
            System.err.println(" Aucun emprunt trouvé avec l'ID: " + idEmprunt);
        }
    }

    
    public double calculerPenalite(int idEmprunt) {
        Optional<Emprunt> optEmprunt = empruntDAO.findById(idEmprunt);
        if (!optEmprunt.isPresent()) {
            return 0;
        }

        Emprunt emprunt = optEmprunt.get();
        
        if (emprunt.getDateRetourEffective() == null) {
            return 0;
        }

        int joursRetard = DateUtils.calculerJoursRetard(
            emprunt.getDateRetourPrevue(),
            emprunt.getDateRetourEffective()
        );
        
        return DateUtils.calculerPenalite(joursRetard);
    }

    public List<Emprunt> getHistoriqueEmprunts(int idMembre) {
        return empruntDAO.findByMembre(idMembre);
    }

    
    public List<Emprunt> getEmpruntsEnCours() {
        return empruntDAO.findEnCours();
    }

    
    public void supprimerMembre(int id) {
        membreDAO.delete(id);
        System.out.println(" Membre supprimé avec succès.");
    }

    
    public void supprimerEmprunt(int id) {
        empruntDAO.delete(id);
        System.out.println(" Emprunt supprimé avec succès.");
    }

    
    public void mettreAJourMembre(Membre membre) {
        membreDAO.update(membre);
        System.out.println("Membre mis à jour avec succès.");
    }
}
