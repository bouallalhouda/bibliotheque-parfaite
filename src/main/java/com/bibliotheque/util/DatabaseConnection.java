package com.bibliotheque.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;
    
    // Paramètres de connexion
    private String url = "jdbc:mysql://localhost:3306/bibliotheque";
    private String username = "root";
    private String password = "";

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connexion MySQL établie !");
            
            verifierTables();
            
        } catch (ClassNotFoundException e) {
            System.err.println(" Driver MySQL introuvable !");
            throw new SQLException("Driver MySQL manquant", e);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
    
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            instance = null;
            System.out.println("🔌 Connexion MySQL fermée.");
        }
    }

    /**
     * Vérifie simplement les tables sans les recréer
     */
    private void verifierTables() {
        try (Statement stmt = connection.createStatement()) {
            
            // Vérifie si la table livre existe et a des données
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM livre");
            if (rs.next()) {
                System.out.println(" Table 'livre' : " + rs.getInt("count") + " livres trouvés");
            }
            
            // Si la table est vide, ajoute des données de test
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM livre WHERE 1");
            if (rs.next() && rs.getInt("count") == 0) {
                System.out.println("➕ Ajout de livres de test...");
                stmt.execute("INSERT INTO livre (isbn, titre, auteur, annee_publication, disponible) VALUES " +
                    "('978-001', 'Harry Potter', 'J.K. Rowling', 1997, true), " +
                    "('978-002', 'Le Petit Prince', 'Antoine', 1943, false), " +
                    "('978-003', '1984', 'George Orwell', 1949, true)");
                System.out.println(" 3 livres ajoutés");
            }
            
        } catch (SQLException e) {
            System.err.println("  Vérification tables: " + e.getMessage());
            
            // Si la table n'existe pas du tout, la crée
            if (e.getMessage().contains("Table 'bibliotheque.livre' doesn't exist")) {
                System.out.println(" Création table 'livre'...");
                creerTableLivre();
            }
        }
    }
    
    private void creerTableLivre() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE livre (" +
                "isbn VARCHAR(20) PRIMARY KEY, " +
                "titre VARCHAR(100) NOT NULL, " +
                "auteur VARCHAR(100) NOT NULL, " +
                "annee_publication INT, " +
                "disponible BOOLEAN DEFAULT TRUE)");
            
            System.out.println(" Table 'livre' créée");
            
            //  données de test
            stmt.execute("INSERT INTO livre (isbn, titre, auteur, annee_publication, disponible) VALUES " +
                "('978-001', 'Harry Potter', 'J.K. Rowling', 1997, true), " +
                "('978-002', 'Le Petit Prince', 'Antoine', 1943, false), " +
                "('978-003', '1984', 'George Orwell', 1949, true)");
            
            System.out.println(" 3 livres ajoutés");
            
        } catch (SQLException e) {
            System.err.println(" Erreur création table: " + e.getMessage());
        }
    }
}
