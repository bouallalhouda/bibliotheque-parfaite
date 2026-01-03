package com.bibliotheque.service;

import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.model.Membre;
import com.bibliotheque.exception.ValidationException;

import java.util.List;

public class BibliothequeService {

    private MembreDAO membreDAO;

    public BibliothequeService(MembreDAO membreDAO) {
        this.membreDAO = membreDAO;
    }

    public void ajouterMembre(Membre membre) throws ValidationException {
        if (!membre.getEmail().contains("@")) {
            throw new ValidationException("Email invalide");
        }
        membreDAO.save(membre);
    }

    public void activerDesactiver(int id) {
        Membre m = membreDAO.findById(id);
        if (m != null) {
            m.setActif(!m.isActif());
            membreDAO.update(m);
        }
    }

    public List<Membre> listerMembres() {
        return membreDAO.findAll();
    }

    public Membre chercherParEmail(String email) {
        return membreDAO.findByEmail(email);
    }
}

