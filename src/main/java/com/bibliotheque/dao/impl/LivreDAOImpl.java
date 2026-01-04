package com.bibliotheque.dao.impl;

import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.model.Livre;
import com.bibliotheque.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivreDAOImpl implements LivreDAO {
    private Connection connection;

    public LivreDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setAnneePublication(rs.getInt("annee_publication"));
        livre.setDisponible(rs.getBoolean("disponible"));
        return livre;
    }

    @Override
    public Optional<Livre> findById(int id) {
        
        System.err.println("  findById() ne fonctionne pas - table n'a pas de colonne 'id'");
        return Optional.empty();
    }

    @Override
    public List<Livre> findAll() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            System.out.println(" DAO: " + livres.size() + " livres trouvés");
        } catch (SQLException e) {
            System.err.println(" Erreur DAO findAll: " + e.getMessage());
        }
        return livres;
    }

    @Override
    public void save(Livre livre) {
        String sql = "INSERT INTO livre (isbn, titre, auteur, annee_publication, disponible) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setString(3, livre.getAuteur());
            stmt.setInt(4, livre.getAnneePublication());
            stmt.setBoolean(5, livre.isDisponible());
            stmt.executeUpdate();
            System.out.println(" DAO: Livre sauvegardé - " + livre.getTitre());
        } catch (SQLException e) {
            System.err.println(" Erreur DAO save: " + e.getMessage());
        }
    }

    @Override
    public void update(Livre livre) {
        String sql = "UPDATE livre SET titre = ?, auteur = ?, annee_publication = ?, disponible = ? WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setInt(3, livre.getAnneePublication());
            stmt.setBoolean(4, livre.isDisponible());
            stmt.setString(5, livre.getIsbn()); // Utilise ISBN comme identifiant
            stmt.executeUpdate();
            System.out.println(" DAO: Livre mis à jour - " + livre.getTitre());
        } catch (SQLException e) {
            System.err.println(" Erreur DAO update: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        
        System.err.println("  delete(int id) ne fonctionne pas - utiliser ISBN");
    }

    
    public void deleteByIsbn(String isbn) {
        String sql = "DELETE FROM livre WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
            System.out.println(" DAO: Livre supprimé (ISBN: " + isbn + ")");
        } catch (SQLException e) {
            System.err.println(" Erreur DAO deleteByIsbn: " + e.getMessage());
        }
    }

    @Override
    public Livre findByIsbn(String isbn) {
        String sql = "SELECT * FROM livre WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLivre(rs);
            }
        } catch (SQLException e) {
            System.err.println(" Erreur DAO findByIsbn: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Livre> findByTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE titre LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + titre + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Erreur DAO findByTitre: " + e.getMessage());
        }
        return livres;
    }

    @Override
    public List<Livre> findByAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE auteur LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + auteur + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Erreur DAO findByAuteur: " + e.getMessage());
        }
        return livres;
    }

    @Override
    public List<Livre> findDisponibles() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE disponible = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Erreur DAO findDisponibles: " + e.getMessage());
        }
        return livres;
    }
}
