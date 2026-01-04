package com.bibliotheque.dao;

import com.bibliotheque.model.Membre;
import java.util.List;
import java.util.Optional;

public interface MembreDAO extends DAO<Membre> {
    
    Membre findByEmail(String email);
    
   
    List<Membre> findActifs();
}
