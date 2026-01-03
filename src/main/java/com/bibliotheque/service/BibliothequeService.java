package com.bibliotheque.service;

import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.dao.impl.MembreDAOImpl;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;

import java.util.ArrayList;
import java.util.List;

public class BibliothequeService {
	private final MembreDAO membreDAO;

	public BibliothequeService() {
		this.membreDAO = new MembreDAOImpl();
	}

	// Livres (minimal)
	public List<Livre> getTousLesLivres() {
		return new ArrayList<>();
	}

	public void ajouterLivre(Livre livre) {
		// implémentation minimale pour compilation
	}

	// Membres
	public List<Membre> getTousLesMembres() {
		try {
			return membreDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void ajouterMembre(Membre membre) {
		try {
			membreDAO.save(membre);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void supprimerMembre(int id) {
		try {
			membreDAO.delete(id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void mettreAJourMembre(Membre membre) {
		try {
			membreDAO.update(membre);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
