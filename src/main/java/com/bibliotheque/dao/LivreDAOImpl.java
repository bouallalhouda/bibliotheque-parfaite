package com.bibliotheque.dao;

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

    @Override
    public void save(Livre livre) {
        String sql = "INSERT INTO livres (isbn, titre, auteur, disponible) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setString(3, livre.getAuteur());
            stmt.setBoolean(4, livre.isDisponible());
            stmt.executeUpdate();
            System.out.println("📚 Livre sauvegardé : " + livre.getTitre());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Livre> findAll() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Livre livre = mapResultSetToLivre(rs);
                livres.add(livre);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return livres;
    }

    @Override
    public Optional<Livre> findById(int id) {
        String sql = "SELECT * FROM livres WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLivre(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Livre livre) {
        String sql = "UPDATE livres SET isbn = ?, titre = ?, auteur = ?, disponible = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setString(3, livre.getAuteur());
            stmt.setBoolean(4, livre.isDisponible());
            stmt.setInt(5, livre.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM livres WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId(rs.getInt("id"));
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setDisponible(rs.getBoolean("disponible"));
        return livre;
    }
} 
