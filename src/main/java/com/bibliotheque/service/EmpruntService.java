package com.bibliotheque.service;

import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.dao.impl.LivreDAOImpl;
import com.bibliotheque.dao.MembreDAOImpl;
import com.bibliotheque.dao.impl.EmpruntDAOImpl;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.MembreInactifException;
import com.bibliotheque.exception.LimiteEmpruntDepasseeException;

import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class EmpruntService {
    
    private LivreDAO livreDAO;
    private MembreDAO membreDAO;
    private EmpruntDAO empruntDAO;
    
    // Constructeur qui gère les SQLException
    public EmpruntService() {
        try {
            this.livreDAO = new LivreDAOImpl();
            this.membreDAO = new MembreDAOImpl();
            this.empruntDAO = new EmpruntDAOImpl();
        } catch (SQLException e) {
            // Transforme en RuntimeException pour ne pas devoir déclarer throws
            throw new RuntimeException("Erreur d'initialisation des DAO: " + e.getMessage(), e);
        }
    }
    
    public Emprunt emprunterLivre(String isbn, int membreId) 
        throws LivreIndisponibleException, 
               MembreInactifException, 
               LimiteEmpruntDepasseeException {
        
        // 1. Vérifier si le livre existe et est disponible
        Livre livre = livreDAO.findByIsbn(isbn);
        if (livre == null) {
            throw new LivreIndisponibleException("Livre avec ISBN " + isbn + " introuvable");
        }
        
        if (!livre.isDisponible()) {
            throw new LivreIndisponibleException("Livre avec ISBN " + isbn + " n'est pas disponible");
        }
        
        // 2. Vérifier si le membre existe et est actif
        Membre membre = membreDAO.findById(membreId).orElse(null);
        if (membre == null) {
            throw new MembreInactifException("Membre avec ID " + membreId + " introuvable");
        }
        
        if (!membre.isActif()) {
            throw new MembreInactifException("Membre avec ID " + membreId + " est inactif");
        }
        
        // 3. Vérifier la limite d'emprunts (max 3)
        int empruntsEnCours = empruntDAO.countEmpruntsEnCours(membreId);
        if (empruntsEnCours >= 3) {
            throw new LimiteEmpruntDepasseeException(
                "Membre " + membreId + " a déjà " + empruntsEnCours + " emprunts en cours (max: 3)"
            );
        }
        
        // 4. Créer l'emprunt
        Emprunt emprunt = new Emprunt();
        emprunt.setLivre(livre);
        emprunt.setMembre(membre);
        emprunt.setDateEmprunt(new Date());
        emprunt.setRetourne(false); // IMPORTANT: initialiser à false
        
        // Date de retour prévue: aujourd'hui + 14 jours
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 14);
        emprunt.setDateRetourPrevue(cal.getTime());
        
        // 5. Sauvegarder et mettre à jour la disponibilité
        empruntDAO.save(emprunt);
        livre.setDisponible(false);
        livreDAO.update(livre);
        
        return emprunt;
    }
    
    public void retournerLivre(int empruntId) {
        try {
            Emprunt emprunt = empruntDAO.findById(empruntId).orElse(null);
            if (emprunt != null && !emprunt.isRetourne()) { // Vérifier isRetourne() au lieu de dateRetourReelle
                emprunt.setDateRetourReelle(new Date()); // CORRIGÉ: setDateRetourReelle (pas Effective)
                emprunt.setRetourne(true); // Marquer comme retourné
                empruntDAO.update(emprunt);
                
                Livre livre = emprunt.getLivre();
                livre.setDisponible(true);
                livreDAO.update(livre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public double calculerPenalite(int empruntId) {
        Emprunt emprunt = empruntDAO.findById(empruntId).orElse(null);
        if (emprunt == null || emprunt.isRetourne()) { // Vérifier isRetourne() au lieu de dateRetourReelle
            return 0.0;
        }
        
        Date aujourdhui = new Date();
        if (aujourdhui.after(emprunt.getDateRetourPrevue())) {
            long diff = aujourdhui.getTime() - emprunt.getDateRetourPrevue().getTime();
            long joursRetard = diff / (1000 * 60 * 60 * 24);
            return joursRetard * 0.50;
        }
        
        return 0.0;
    }
    
    public List<Emprunt> getEmpruntsEnCours() {
        return empruntDAO.findEnCours();
    }
    
    public List<Emprunt> getEmpruntsEnRetard() {
        List<Emprunt> empruntsEnCours = empruntDAO.findEnCours();
        List<Emprunt> empruntsEnRetard = new ArrayList<>();
        Date aujourdhui = new Date();
        
        for (Emprunt emprunt : empruntsEnCours) {
            if (aujourdhui.after(emprunt.getDateRetourPrevue())) {
                empruntsEnRetard.add(emprunt);
            }
        }
        
        return empruntsEnRetard;
    }
}
