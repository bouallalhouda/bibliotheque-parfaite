package com.bibliotheque.dao;

import com.bibliotheque.model.Livre;
import java.util.List;

public interface LivreDAO extends DAO<Livre> {
    Livre findByIsbn(String isbn);
    List<Livre> findByTitre(String titre);
    List<Livre> findByAuteur(String auteur);
    List<Livre> findDisponibles();
}

