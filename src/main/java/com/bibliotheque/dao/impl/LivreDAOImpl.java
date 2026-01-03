package com.bibliotheque.dao.impl;



import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.model.Livre;
import com.bibliotheque.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LivreDAOImpl implements LivreDAO {
    
    private Connection connection;
    
    public LivreDAOImpl() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // === REQUÊTES SQL ===
    private static final String FIND_BY_ID = 
        "SELECT * FROM livres WHERE id_livre = ?";
    
    private static final String FIND_ALL = 
        "SELECT * FROM livres ORDER BY titre";
    
    private static final String INSERT = 
        "INSERT INTO livres (titre, auteur, isbn, annee_publication, " +
        "categorie, quantite_totale, quantite_disponible) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE = 
        "UPDATE livres SET titre = ?, auteur = ?, isbn = ?, " +
        "annee_publication = ?, categorie = ?, quantite_totale = ?, " +
        "quantite_disponible = ? WHERE id_livre = ?";
    
    private static final String DELETE = 
        "DELETE FROM livres WHERE id_livre = ?";
    
    private static final String FIND_BY_TITRE = 
        "SELECT * FROM livres WHERE LOWER(titre) LIKE LOWER(?) ORDER BY titre";
    
    private static final String FIND_BY_AUTEUR = 
        "SELECT * FROM livres WHERE LOWER(auteur) LIKE LOWER(?) ORDER BY auteur";
    
    private static final String FIND_BY_ISBN = 
        "SELECT * FROM livres WHERE isbn = ?";
    
    private static final String FIND_BY_CATEGORIE = 
        "SELECT * FROM livres WHERE LOWER(categorie) LIKE LOWER(?) ORDER BY categorie";
    
    private static final String FIND_BY_ANNEE = 
        "SELECT * FROM livres WHERE annee_publication = ? ORDER BY titre";
    
    private static final String SEARCH_GLOBAL = 
        "SELECT * FROM livres WHERE " +
        "LOWER(titre) LIKE LOWER(?) OR " +
        "LOWER(auteur) LIKE LOWER(?) OR " +
        "LOWER(isbn) LIKE LOWER(?) OR " +
        "LOWER(categorie) LIKE LOWER(?) " +
        "ORDER BY titre";
    
    private static final String COUNT_TOTAL = 
        "SELECT COUNT(*) FROM livres";
    
    private static final String FIND_CATEGORIES = 
        "SELECT DISTINCT categorie FROM livres WHERE categorie IS NOT NULL ORDER BY categorie";
    
    private static final String ISBN_EXISTS = 
        "SELECT COUNT(*) FROM livres WHERE isbn = ?";
    
    private static final String ISBN_EXISTS_OTHER = 
        "SELECT COUNT(*) FROM livres WHERE isbn = ? AND id_livre != ?";
    
    // === MÉTHODE UTILITAIRE POUR MAPPER UN RESULT SET ===
    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId(rs.getInt("id_livre"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setIsbn(rs.getString("isbn"));
        
        // Convertir année en LocalDate
        int annee = rs.getInt("annee_publication");
        if (!rs.wasNull()) {
            livre.setDatePublication(LocalDate.of(annee, 1, 1));
        }
        
        livre.setCategorie(rs.getString("categorie"));
        livre.setQuantiteTotale(rs.getInt("quantite_totale"));
        livre.setQuantiteDisponible(rs.getInt("quantite_disponible"));
        
        // Définir la disponibilité
        livre.setDisponible(rs.getInt("quantite_disponible") > 0);
        
        return livre;
    }
    
    // === IMPLÉMENTATION DES MÉTHODES ===
    @Override
    public Livre findById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToLivre(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Livre> findAll() {
        List<Livre> livres = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL)) {
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public int save(Livre livre) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt(4, livre.getDatePublication().getYear());
            stmt.setString(5, livre.getCategorie());
            stmt.setInt(6, livre.getQuantiteTotale());
            stmt.setInt(7, livre.getQuantiteDisponible());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Retourne l'ID généré
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Échec
    }
    
    @Override
    public boolean update(Livre livre) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt(4, livre.getDatePublication().getYear());
            stmt.setString(5, livre.getCategorie());
            stmt.setInt(6, livre.getQuantiteTotale());
            stmt.setInt(7, livre.getQuantiteDisponible());
            stmt.setInt(8, livre.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Livre> findByTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_TITRE)) {
            stmt.setString(1, "%" + titre + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public List<Livre> findByAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_AUTEUR)) {
            stmt.setString(1, "%" + auteur + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public List<Livre> findByIsbn(String isbn) {
        List<Livre> livres = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ISBN)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public List<Livre> findByCategorie(String categorie) {
        List<Livre> livres = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_CATEGORIE)) {
            stmt.setString(1, "%" + categorie + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public List<Livre> findByAnnee(int annee) {
        List<Livre> livres = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ANNEE)) {
            stmt.setInt(1, annee);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public List<Livre> searchGlobal(String motCle) {
        List<Livre> livres = new ArrayList<>();
        
        if (motCle == null || motCle.trim().isEmpty()) {
            return findAll();
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(SEARCH_GLOBAL)) {
            String searchTerm = "%" + motCle + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public List<Livre> searchAdvanced(String titre, String auteur, String categorie, String isbn, Integer anneeMin, Integer anneeMax) {
        List<Livre> livres = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM livres WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (titre != null && !titre.trim().isEmpty()) {
            sql.append(" AND LOWER(titre) LIKE LOWER(?)");
            params.add("%" + titre + "%");
        }
        if (auteur != null && !auteur.trim().isEmpty()) {
            sql.append(" AND LOWER(auteur) LIKE LOWER(?)");
            params.add("%" + auteur + "%");
        }
        if (categorie != null && !categorie.trim().isEmpty()) {
            sql.append(" AND LOWER(categorie) LIKE LOWER(?)");
            params.add("%" + categorie + "%");
        }
        if (isbn != null && !isbn.trim().isEmpty()) {
            sql.append(" AND isbn = ?");
            params.add(isbn);
        }
        if (anneeMin != null) {
            sql.append(" AND annee_publication >= ?");
            params.add(anneeMin);
        }
        if (anneeMax != null) {
            sql.append(" AND annee_publication <= ?");
            params.add(anneeMax);
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }
    
    @Override
    public int countTotalLivres() {
        String sql = "SELECT COUNT(*) FROM livres";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public int countLivresDisponibles() {
        String sql = "SELECT SUM(quantite_disponible) FROM livres";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public int countLivresEmpruntes() {
        String sql = "SELECT COUNT(*) FROM emprunts WHERE date_retour_reelle IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public List<String> findAllCategories() {
        List<String> categories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_CATEGORIES)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    // ... autres méthodes d'implémentation ...
    
    @Override
    public boolean isbnExists(String isbn) {
        try (PreparedStatement stmt = connection.prepareStatement(ISBN_EXISTS)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isbnExistsForOtherBook(String isbn, int idLivre) {
        try (PreparedStatement stmt = connection.prepareStatement(ISBN_EXISTS_OTHER)) {
            stmt.setString(1, isbn);
            stmt.setInt(2, idLivre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateQuantite(int idLivre, int nouvelleQuantite) {
        // Assuming nouvelleQuantite is totale, and disponible = totale - emprunted
        // But for simplicity, set totale = nouvelleQuantite, disponible = nouvelleQuantite
        String sql = "UPDATE livres SET quantite_totale = ?, quantite_disponible = ? WHERE id_livre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, nouvelleQuantite);
            stmt.setInt(3, idLivre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean incrementerQuantiteDisponible(int idLivre) {
        String sql = "UPDATE livres SET quantite_disponible = quantite_disponible + 1 WHERE id_livre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idLivre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean decrementerQuantiteDisponible(int idLivre) {
        String sql = "UPDATE livres SET quantite_disponible = quantite_disponible - 1 WHERE id_livre = ? AND quantite_disponible > 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idLivre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
