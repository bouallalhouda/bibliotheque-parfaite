package com.bibliotheque.dao;

import com.bibliotheque.model.Emprunt;
import java.util.List;

public interface EmpruntDAO extends DAO<Emprunt> {
    // Récupère tous les emprunts d'un membre et retourne une liste des Emprunts du membre
    List<Emprunt> findByMembre(int idMembre);
    
    // Récupère tous les emprunts en cours et retourne une liste des Emprunts en cours
    List<Emprunt> findEnCours();
    
    // Compte le nombre d'emprunts en cours d'un membre et retourne le nombre d'emprunts en cours
    int countEmpruntsEnCours(int idMembre);
}
