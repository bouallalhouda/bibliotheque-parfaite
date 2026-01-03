package com.bibliotheque.dao;

import com.bibliotheque.model.Membre;
import java.util.List;

public interface MembreDAO {

    void save(Membre membre);

    Membre findById(int id);

    List<Membre> findAll();

    void update(Membre membre);

    void delete(int id);

    Membre findByEmail(String email);

    List<Membre> findActifs();
}

