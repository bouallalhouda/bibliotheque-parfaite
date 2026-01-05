package com.bibliotheque.dao.impl;

import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.MembreDAO;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;
import com.bibliotheque.util.DatabaseConnection;
import com.bibliotheque.dao.impl.LivreDAOImpl;   // Pour new LivreDAOImpl()
import com.bibliotheque.dao.MembreDAOImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDAOImpl implements EmpruntDAO {
    private Connection connection;
    private LivreDAO livreDAO;
    private MembreDAO membreDAO;

    public EmpruntDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.livreDAO = new LivreDAOImpl();
        this.membreDAO = new MembreDAOImpl();
    }

    /**
     * Mappe un ResultSet à un objet Emprunt
     */
    private Emprunt mapResultSetToEmprunt(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getInt("id"));
        
        // Conversion de java.sql.Date à java.util.Date
        java.sql.Date sqlDateEmprunt = rs.getDate("date_emprunt");
        if (sqlDateEmprunt != null) {
            emprunt.setDateEmprunt(new java.util.Date(sqlDateEmprunt.getTime()));
        }
        
        java.sql.Date sqlDateRetourPrevue = rs.getDate("date_retour_prevue");
        if (sqlDateRetourPrevue != null) {
            emprunt.setDateRetourPrevue(new java.util.Date(sqlDateRetourPrevue.getTime()));
        }
        
        java.sql.Date sqlDateRetourReelle = rs.getDate("date_retour_reelle");
        if (sqlDateRetourReelle != null) {
            emprunt.setDateRetourReelle(new java.util.Date(sqlDateRetourReelle.getTime()));
        }
        
        emprunt.setRetourne(rs.getBoolean("retourne"));
        
        // Récupérer le livre par ISBN (pas par ID)
        String isbnLivre = rs.getString("livre_isbn");
        if (isbnLivre != null) {
            Livre livre = livreDAO.findByIsbn(isbnLivre);
            emprunt.setLivre(livre);
        }
        
        // Récupérer le membre par ID
        int idMembre = rs.getInt("membre_id");
        Optional<Membre> membreOpt = membreDAO.findById(idMembre);
        membreOpt.ifPresent(emprunt::setMembre);
        
        return emprunt;
    }

    @Override
    public Optional<Emprunt> findById(int id) {
        String sql = "SELECT * FROM emprunts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche emprunt par ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Emprunt> findAll() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération tous les emprunts: " + e.getMessage());
        }
        return emprunts;
    }

    @Override
    public void save(Emprunt emprunt) {
        String sql = "INSERT INTO emprunts (date_emprunt, date_retour_prevue, date_retour_reelle, retourne, livre_isbn, membre_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Conversion java.util.Date → java.sql.Date
            if (emprunt.getDateEmprunt() != null) {
                stmt.setDate(1, new java.sql.Date(emprunt.getDateEmprunt().getTime()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            
            if (emprunt.getDateRetourPrevue() != null) {
                stmt.setDate(2, new java.sql.Date(emprunt.getDateRetourPrevue().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            if (emprunt.getDateRetourReelle() != null) {
                stmt.setDate(3, new java.sql.Date(emprunt.getDateRetourReelle().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setBoolean(4, emprunt.isRetourne());
            stmt.setString(5, emprunt.getLivre().getIsbn()); // Utiliser ISBN, pas ID
            stmt.setInt(6, emprunt.getMembre().getId());
            
            stmt.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                emprunt.setId(rs.getInt(1));
            }
            
            System.out.println("✅ Emprunt inséré avec succès. ID: " + emprunt.getId());
        } catch (SQLException e) {
            System.err.println("Erreur insertion emprunt: " + e.getMessage());
        }
    }

    @Override
    public void update(Emprunt emprunt) {
        String sql = "UPDATE emprunts SET date_emprunt = ?, date_retour_prevue = ?, date_retour_reelle = ?, retourne = ?, livre_isbn = ?, membre_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Conversion java.util.Date → java.sql.Date
            if (emprunt.getDateEmprunt() != null) {
                stmt.setDate(1, new java.sql.Date(emprunt.getDateEmprunt().getTime()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            
            if (emprunt.getDateRetourPrevue() != null) {
                stmt.setDate(2, new java.sql.Date(emprunt.getDateRetourPrevue().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            if (emprunt.getDateRetourReelle() != null) {
                stmt.setDate(3, new java.sql.Date(emprunt.getDateRetourReelle().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setBoolean(4, emprunt.isRetourne());
            stmt.setString(5, emprunt.getLivre().getIsbn()); // Utiliser ISBN, pas ID
            stmt.setInt(6, emprunt.getMembre().getId());
            stmt.setInt(7, emprunt.getId());
            
            stmt.executeUpdate();
            System.out.println("✅ Emprunt mis à jour avec succès. ID: " + emprunt.getId());
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour emprunt: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM emprunts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Emprunt supprimé avec succès. ID: " + id);
        } catch (SQLException e) {
            System.err.println("Erreur suppression emprunt: " + e.getMessage());
        }
    }

    @Override
    public List<Emprunt> findByMembre(int idMembre) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE membre_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche emprunts par membre: " + e.getMessage());
        }
        return emprunts;
    }

    @Override
    public List<Emprunt> findEnCours() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE retourne = false"; // ou date_retour_reelle IS NULL
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération emprunts en cours: " + e.getMessage());
        }
        return emprunts;
    }

    @Override
    public int countEmpruntsEnCours(int idMembre) {
        String sql = "SELECT COUNT(*) as count FROM emprunts WHERE membre_id = ? AND retourne = false";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage emprunts en cours: " + e.getMessage());
        }
        return 0;
    }
}
