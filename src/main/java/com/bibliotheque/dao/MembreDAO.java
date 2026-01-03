package com.bibliotheque.dao;

import com.bibliotheque.model.Membre;
import java.util.List;
import java.util.Optional;

public interface MembreDAO extends DAO<Membre> {
    // Optional findById is inherited from DAO<T>, redeclared here for clarity
    Optional<Membre> findById(int id);

    void save(Membre membre);
    List<Membre> findAll();
    void update(Membre membre);
    void delete(int id);

    Membre findByEmail(String email);
    List<Membre> findActifs();
}
