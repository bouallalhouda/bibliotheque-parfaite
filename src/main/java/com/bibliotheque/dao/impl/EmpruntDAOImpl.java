package com.bibliotheque.dao;

import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;
import com.bibliotheque.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDAOImpl implements EmpruntDAO {
    private Connection connection;

    public EmpruntDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Mappe un ResultSet à un objet Emprunt et throws SQLException si erreur SQL
    private Emprunt mapResultSetToEmprunt(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getInt("id"));
        emprunt.setDateEmprunt(rs.getObject("date_emprunt", LocalDate.class));
        emprunt.setDateRetourPrevue(rs.getObject("date_retour_prevue", LocalDate.class));
        emprunt.setDateRetourEffective(rs.getObject("date_retour_effective", LocalDate.class));
        
        // Récupérer l'ID du livre et chercher le Livre
        int idLivre = rs.getInt("id_livre");
        int idMembre = rs.getInt("id_membre");        
        
        return emprunt;
    }

    @Override
    public Optional<Emprunt> findById(int id) {
        String sql = "SELECT * FROM emprunt WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'emprunt par ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Emprunt> findAll() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les emprunts: " + e.getMessage());
        }
        return emprunts;
    }

    @Override
    public void save(Emprunt emprunt) {
        String sql = "INSERT INTO emprunt (date_emprunt, date_retour_prevue, date_retour_effective, id_livre, id_membre) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, emprunt.getDateEmprunt());
            stmt.setObject(2, emprunt.getDateRetourPrevue());
            stmt.setObject(3, emprunt.getDateRetourEffective());
            stmt.setInt(4, emprunt.getLivre().getId());
            stmt.setInt(5, emprunt.getMembre().getId());
            stmt.executeUpdate();
            System.out.println("✅ Emprunt inséré avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'emprunt: " + e.getMessage());
        }
    }

    @Override
    public void update(Emprunt emprunt) {
        String sql = "UPDATE emprunt SET date_emprunt = ?, date_retour_prevue = ?, date_retour_effective = ?, id_livre = ?, id_membre = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, emprunt.getDateEmprunt());
            stmt.setObject(2, emprunt.getDateRetourPrevue());
            stmt.setObject(3, emprunt.getDateRetourEffective());
            stmt.setInt(4, emprunt.getLivre().getId());
            stmt.setInt(5, emprunt.getMembre().getId());
            stmt.setInt(6, emprunt.getId());
            stmt.executeUpdate();
            System.out.println("✅ Emprunt mis à jour avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'emprunt: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM emprunt WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Emprunt supprimé avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'emprunt: " + e.getMessage());
        }
    }

    @Override
    public List<Emprunt> findByMembre(int idMembre) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE id_membre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des emprunts du membre: " + e.getMessage());
        }
        return emprunts;
    }

    @Override
    public List<Emprunt> findEnCours() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE date_retour_effective IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts en cours: " + e.getMessage());
        }
        return emprunts;
    }

    @Override
    public int countEmpruntsEnCours(int idMembre) {
        String sql = "SELECT COUNT(*) as count FROM emprunt WHERE id_membre = ? AND date_retour_effective IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des emprunts en cours: " + e.getMessage());
        }
        return 0;
    }
}
