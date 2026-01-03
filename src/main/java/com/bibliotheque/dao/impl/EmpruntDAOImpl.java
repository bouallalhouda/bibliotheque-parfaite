package com.bibliotheque.dao.impl;

import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Membre;
import com.bibliotheque.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDAOImpl implements EmpruntDAO {

    private Connection connection;
    private LivreDAOImpl livreDAO;
    private MembreDAOImpl membreDAO;

    public EmpruntDAOImpl() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.livreDAO = new LivreDAOImpl();
            this.membreDAO = new MembreDAOImpl();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Emprunt emprunt) {
        String sql = "INSERT INTO emprunts (livre_isbn, membre_id, date_emprunt, date_retour_prevue, date_retour_reelle) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emprunt.getLivre().getIsbn());
            ps.setInt(2, emprunt.getMembre().getId());
            ps.setDate(3, new java.sql.Date(emprunt.getDateEmprunt().getTime()));
            ps.setDate(4, new java.sql.Date(emprunt.getDateRetourPrevue().getTime()));
            if (emprunt.getDateRetourEffective() != null) {
                ps.setDate(5, new java.sql.Date(emprunt.getDateRetourEffective().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Emprunt> findById(int id) {
        String sql = "SELECT * FROM emprunts WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapResultSetToEmprunt(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Emprunt> findAll() {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToEmprunt(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Emprunt> findByMembre(int membreId) {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE membre_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, membreId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToEmprunt(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Emprunt> findByLivreIsbn(String isbn) {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE livre_isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToEmprunt(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Emprunt> findEmpruntsEnCours() {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE date_retour_reelle IS NULL";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToEmprunt(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Emprunt emprunt) {
        String sql = "UPDATE emprunts SET livre_isbn = ?, membre_id = ?, date_emprunt = ?, date_retour_prevue = ?, date_retour_reelle = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emprunt.getLivre().getIsbn());
            ps.setInt(2, emprunt.getMembre().getId());
            ps.setDate(3, new java.sql.Date(emprunt.getDateEmprunt().getTime()));
            ps.setDate(4, new java.sql.Date(emprunt.getDateRetourPrevue().getTime()));
            if (emprunt.getDateRetourEffective() != null) {
                ps.setDate(5, new java.sql.Date(emprunt.getDateRetourEffective().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setInt(6, emprunt.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to map ResultSet to Emprunt object
    private Emprunt mapResultSetToEmprunt(ResultSet rs) throws SQLException {
        Emprunt e = new Emprunt();
        e.setId(rs.getInt("id"));
        e.setDateEmprunt(rs.getDate("date_emprunt"));
        e.setDateRetourPrevue(rs.getDate("date_retour_prevue"));
        e.setDateRetourEffective(rs.getDate("date_retour_reelle"));

        String isbn = rs.getString("livre_isbn");
        Livre livre = livreDAO.findByIsbn(isbn);
        e.setLivre(livre);

        int membreId = rs.getInt("membre_id");
        Membre membre = membreDAO.findById(membreId).orElse(null);
        e.setMembre(membre);

        return e;
    }
}
