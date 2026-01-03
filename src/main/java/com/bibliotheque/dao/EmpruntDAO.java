package com.bibliotheque.dao;

import com.bibliotheque.model.Emprunt;
import java.util.List;
import java.util.Optional;

public interface EmpruntDAO extends DAO<Emprunt> {
    // Optional findById is inherited from DAO<T>, redeclared here for clarity
    Optional<Emprunt> findById(int id);

    void save(Emprunt emprunt);
    List<Emprunt> findAll();
    void update(Emprunt emprunt);
    void delete(int id);

    List<Emprunt> findByLivreIsbn(String isbn);
    List<Emprunt> findByMembre(int membreId);
    List<Emprunt> findEmpruntsEnCours();
}
