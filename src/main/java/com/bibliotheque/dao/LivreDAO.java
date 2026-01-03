package dao.impl;

import dao.LivreDAO;
import model.Livre;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAOImpl implements LivreDAO {
    private Connection connection;

    // Constructeur - utilise le Singleton
    public LivreDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Livre livre) throws SQLException {
        // ⚠️ TOUJOURS utiliser PreparedStatement, JAMAIS concaténer des strings !
        String sql = "INSERT INTO livres (isbn, titre, auteur, disponible) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getIsbn());  // Le ? devient la valeur
            stmt.setString(2, livre.getTitre());
            stmt.setString(3, livre.getAuteur());
            stmt.setBoolean(4, livre.isDisponible());
            
            stmt.executeUpdate();
            System.out.println("📚 Livre sauvegardé : " + livre.getTitre());
        }
    }

    @Override
    public List<Livre> findAll() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Livre livre = mapResultSetToLivre(rs);
                livres.add(livre);
            }
        }
        return livres;
    }
    
    // Méthode utilitaire pour éviter la duplication
    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId(rs.getInt("id"));
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setDisponible(rs.getBoolean("disponible"));
        return livre;
    }
}"package com.bibliotheque.dao;" 
