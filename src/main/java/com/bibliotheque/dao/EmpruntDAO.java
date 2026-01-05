package com.bibliotheque.dao;

import com.bibliotheque.model.Emprunt;
import java.util.List;

public interface EmpruntDAO extends DAO<Emprunt> {
    List<Emprunt> findByMembre(int idMembre);
    List<Emprunt> findEnCours();
    int countEmpruntsEnCours(int idMembre);
}
