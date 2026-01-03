package com.bibliotheque.service;

import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.dao.impl.EmpruntDAOImpl;
import com.bibliotheque.dao.impl.LivreDAOImpl;
import com.bibliotheque.dao.impl.MembreDAOImpl;
import com.bibliotheque.exception.LimiteEmpruntDepasseeException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.MembreInactifException;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class EmpruntService {

    private final EmpruntDAO empruntDAO;
    private final LivreDAO livreDAO;
    private final MembreDAO membreDAO;

    // Limite d'emprunts par membre
    private static final int LIMITE_EMPRUNTS = 3;

    public EmpruntService() {
        this.empruntDAO = new EmpruntDAOImpl();
        this.livreDAO = new LivreDAOImpl();
        this.membreDAO = new MembreDAOImpl();
    }

    /**
     * Emprunter un livre
     */
    public void emprunterLivre(String isbn, int membreId) throws LivreIndisponibleException, MembreInactifException, LimiteEmpruntDepasseeException {
        // Vérifier si le livre existe et est disponible
        List<Livre> livres = livreDAO.findByIsbn(isbn);
        if (livres.isEmpty()) {
            throw new LivreIndisponibleException("Le livre n'existe pas.");
        }
        Livre livre = livres.get(0);
        if (!livre.isDisponible()) {
            throw new LivreIndisponibleException("Le livre n'est pas disponible.");
        }

        // Vérifier si le membre existe et est actif
        Optional<Membre> membreOpt = membreDAO.findById(membreId);
        if (membreOpt.isEmpty() || !membreOpt.get().isActif()) {
            throw new MembreInactifException("Le membre n'est pas actif.");
        }

        // Vérifier la limite d'emprunts
        List<Emprunt> empruntsEnCours = empruntDAO.findEmpruntsEnCours();
        long empruntsDuMembre = empruntsEnCours.stream()
                .filter(e -> e.getMembre() != null && e.getMembre().getId() == membreId)
                .count();
        if (empruntsDuMembre >= LIMITE_EMPRUNTS) {
            throw new LimiteEmpruntDepasseeException("Limite d'emprunts dépassée.");
        }

        // Créer l'emprunt
        Date dateEmprunt = new Date();
        Date dateRetourPrevue = new Date(dateEmprunt.getTime() + (14 * 24 * 60 * 60 * 1000L)); // 14 jours

        Emprunt emprunt = new Emprunt(0, dateEmprunt, dateRetourPrevue, livre, membreOpt.get());
        empruntDAO.save(emprunt);

        // Marquer le livre comme indisponible
        livre.setDisponible(false);
        livreDAO.update(livre);
    }

    /**
     * Retourner un livre
     */
    public void retournerLivre(int empruntId) {
        Optional<Emprunt> empruntOpt = empruntDAO.findById(empruntId);
        if (empruntOpt.isPresent()) {
            Emprunt emprunt = empruntOpt.get();
            emprunt.setDateRetourEffective(new Date());
            empruntDAO.update(emprunt);

            // Marquer le livre comme disponible
            if (emprunt.getLivre() != null) {
                emprunt.getLivre().setDisponible(true);
                livreDAO.update(emprunt.getLivre());
            }
        }
    }

    /**
     * Obtenir tous les emprunts
     */
    public List<Emprunt> getTousLesEmprunts() {
        return empruntDAO.findAll();
    }

    /**
     * Obtenir les emprunts en cours
     */
    public List<Emprunt> getEmpruntsEnCours() {
        return empruntDAO.findEmpruntsEnCours();
    }

    /**
     * Obtenir les emprunts d'un membre
     */
    public List<Emprunt> getEmpruntsParMembre(int membreId) {
        return empruntDAO.findByMembre(membreId);
    }

    /**
     * Obtenir les emprunts d'un livre
     */
    public List<Emprunt> getEmpruntsParLivre(String isbn) {
        return empruntDAO.findByLivreIsbn(isbn);
    }
}
