package com.bibliotheque.service;

import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.impl.LivreDAOImpl;
import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.dao.MembreDAOImpl;
import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.dao.impl.EmpruntDAOImpl;
import com.bibliotheque.exception.ValidationException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.MembreInactifException;
import com.bibliotheque.exception.LimiteEmpruntDepasseeException;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.util.StringValidator;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
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

    // ======================== MÉTHODES POUR LES LIVRES ========================

    public void ajouterLivre(Livre livre) throws ValidationException {
        if (livre.getIsbn() == null || livre.getIsbn().trim().isEmpty()) {
            throw new ValidationException("L'ISBN ne peut pas être vide.");
        }
        if (!StringValidator.isValidIsbn(livre.getIsbn())) {
            throw new ValidationException("ISBN invalide: " + livre.getIsbn());
        }
        livreDAO.save(livre);
        System.out.println("Livre ajouté: " + livre.getTitre());
    }

    public void modifierLivre(String isbn, Livre nouveau) {
        livreDAO.update(nouveau);
        System.out.println("Livre mis à jour: " + isbn);
    }

    public void supprimerLivre(String isbn) {
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bibliotheque", "root", "");
            String sql = "DELETE FROM livre WHERE isbn = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            int rowsDeleted = stmt.executeUpdate();
            stmt.close();
            conn.close();
            
            if (rowsDeleted > 0) {
                System.out.println("Livre supprimé: " + isbn);
            } else {
                System.err.println("Livre introuvable: " + isbn);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur suppression: " + e.getMessage());
        }
    }

    public List<Livre> rechercherLivres(String critere) {
        List<Livre> resultats = new ArrayList<>();
        resultats.addAll(livreDAO.findByTitre(critere));
        
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

    // ======================== MÉTHODES POUR LES MEMBRES ========================

    public void ajouterMembre(Membre membre) throws ValidationException {
        if (membre.getEmail() == null || membre.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email vide");
        }
        if (!StringValidator.isValidEmail(membre.getEmail())) {
            throw new ValidationException("Email invalide: " + membre.getEmail());
        }
        membreDAO.save(membre);
        System.out.println("Membre ajouté: " + membre.getPrenom() + " " + membre.getNom());
    }

    public void modifierMembre(int id, Membre nouveau) {
        Optional<Membre> optMembre = membreDAO.findById(id);
        if (optMembre.isPresent()) {
            nouveau.setId(id);
            membreDAO.update(nouveau);
            System.out.println("Membre mis à jour: " + id);
        } else {
            System.err.println("Membre introuvable: " + id);
        }
    }

    public void activerDesactiverMembre(int id) {
        Optional<Membre> optMembre = membreDAO.findById(id);
        if (optMembre.isPresent()) {
            Membre membre = optMembre.get();
            membre.setActif(!membre.isActif());
            membreDAO.update(membre);
            String statut = membre.isActif() ? "activé" : "désactivé";
            System.out.println("Membre " + statut + ": " + id);
        } else {
            System.err.println("Membre introuvable: " + id);
        }
    }

    public List<Membre> getTousLesMembres() {
        return membreDAO.findAll();
    }

    public List<Membre> getMembresActifs() {
        return membreDAO.findActifs();
    }

    public Membre rechercherMembreParEmail(String email) {
        return membreDAO.findByEmail(email);
    }

    // ======================== MÉTHODES POUR LES EMPRUNTS ========================

    public Emprunt emprunterLivre(String isbn, int idMembre) 
            throws LivreIndisponibleException, MembreInactifException, LimiteEmpruntDepasseeException {
        
        // 1. Vérifier livre
        Livre livre = livreDAO.findByIsbn(isbn);
        if (livre == null) {
            throw new LivreIndisponibleException("Livre introuvable: " + isbn);
        }
        if (!livre.isDisponible()) {
            throw new LivreIndisponibleException("Livre non disponible: " + isbn);
        }

        // 2. Vérifier membre
        Optional<Membre> optMembre = membreDAO.findById(idMembre);
        if (!optMembre.isPresent()) {
            throw new MembreInactifException("Membre introuvable: " + idMembre);
        }
        Membre membre = optMembre.get();
        if (!membre.isActif()) {
            throw new MembreInactifException("Membre inactif: " + idMembre);
        }

        // 3. Vérifier limite
        int nbEmpruntsEnCours = empruntDAO.countEmpruntsEnCours(idMembre);
        if (nbEmpruntsEnCours >= 3) {
            throw new LimiteEmpruntDepasseeException("Limite atteinte: " + nbEmpruntsEnCours + " emprunts");
        }

        // 4. Créer emprunt
        Emprunt emprunt = new Emprunt();
        emprunt.setLivre(livre);
        emprunt.setMembre(membre);
        emprunt.setDateEmprunt(new Date());
        emprunt.setRetourne(false);
        
        // Date retour: aujourd'hui + 14 jours
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 14);
        emprunt.setDateRetourPrevue(cal.getTime());
        
        // 5. Sauvegarder
        livre.setDisponible(false);
        livreDAO.update(livre);
        empruntDAO.save(emprunt);
        
        System.out.println("Emprunt créé: " + livre.getTitre() + " pour " + membre.getPrenom());
        return emprunt;
    }

    public void retournerLivre(int idEmprunt) {
        Optional<Emprunt> optEmprunt = empruntDAO.findById(idEmprunt);
        if (optEmprunt.isPresent()) {
            Emprunt emprunt = optEmprunt.get();
            
            emprunt.setDateRetourReelle(new Date());
            emprunt.setRetourne(true);
            
            Livre livre = emprunt.getLivre();
            livre.setDisponible(true);
            
            empruntDAO.update(emprunt);
            livreDAO.update(livre);
            
            System.out.println("Livre retourné: " + livre.getTitre());
            
            // Calcul pénalité
            Date datePrevue = emprunt.getDateRetourPrevue();
            Date dateReelle = emprunt.getDateRetourReelle();
            
            if (dateReelle.after(datePrevue)) {
                long diff = dateReelle.getTime() - datePrevue.getTime();
                long joursRetard = diff / (1000 * 60 * 60 * 24);
                double penalite = joursRetard * 0.50;
                System.out.println("Retard: " + joursRetard + " jours, Pénalité: " + penalite + "€");
            }
            
        } else {
            System.err.println("Emprunt introuvable: " + idEmprunt);
        }
    }

    public double calculerPenalite(int idEmprunt) {
        Optional<Emprunt> optEmprunt = empruntDAO.findById(idEmprunt);
        if (!optEmprunt.isPresent()) {
            return 0.0;
        }

        Emprunt emprunt = optEmprunt.get();
        
        if (emprunt.getDateRetourReelle() == null) {
            return 0.0;
        }

        Date datePrevue = emprunt.getDateRetourPrevue();
        Date dateReelle = emprunt.getDateRetourReelle();
        
        if (dateReelle.after(datePrevue)) {
            long diff = dateReelle.getTime() - datePrevue.getTime();
            long joursRetard = diff / (1000 * 60 * 60 * 24);
            return joursRetard * 0.50;
        }
        
        return 0.0;
    }

    public List<Emprunt> getHistoriqueEmprunts(int idMembre) {
        return empruntDAO.findByMembre(idMembre);
    }

    public List<Emprunt> getEmpruntsEnCours() {
        return empruntDAO.findEnCours();
    }

    public void supprimerMembre(int id) {
        membreDAO.delete(id);
        System.out.println("Membre supprimé: " + id);
    }

    public void supprimerEmprunt(int id) {
        empruntDAO.delete(id);
        System.out.println("Emprunt supprimé: " + id);
    }

    public void mettreAJourMembre(Membre membre) {
        membreDAO.update(membre);
        System.out.println("Membre mis à jour: " + membre.getId());
    }
}
