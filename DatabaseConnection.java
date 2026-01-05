package com.bibliotheque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;
    
    // CONFIGURATION XAMPP
    private String url = "jdbc:mysql://localhost:3306/";
    private String username = "root";
    private String password = ""; // XAMPP par défaut 
    
    private DatabaseConnection() throws SQLException {
        try {
            // Charge le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("🔧 Connexion à XAMPP MySQL...");
            
            // ÉTAPE 1: Connexion SANS base spécifique
            Connection tempConn = DriverManager.getConnection(url, username, password);
            System.out.println(" Connexion MySQL XAMPP établie !");
            
            // ÉTAPE 2: Crée la base si elle n'existe pas
            try (Statement stmt = tempConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS bibliotheque");
                System.out.println(" Base 'bibliotheque' créée/vérifiée");
                
                // Utilise la base
                stmt.execute("USE bibliotheque");
            }
            
            // Ferme la connexion temporaire
            tempConn.close();
            
            // ÉTAPE 3: Reconnexion À la base bibliotheque
            this.connection = DriverManager.getConnection(url + "bibliotheque", username, password);
            System.out.println(" Connecté à la base 'bibliotheque'");
            
            // ÉTAPE 4: Crée les tables et données
            initialiserBaseDeDonnees();
            
        } catch (ClassNotFoundException e) {
            System.err.println(" Driver MySQL introuvable !");
            throw new SQLException("Driver MySQL manquant", e);
        }
    }
    
    
    private void creerTablesSiAbsentes() {
        System.out.println("🔧 Création des tables...");
        
        try (Statement stmt = connection.createStatement()) {
            
            // Table livres
            stmt.execute("CREATE TABLE IF NOT EXISTS livres (" +
                "isbn VARCHAR(20) PRIMARY KEY, " +
                "titre VARCHAR(100) NOT NULL, " +
                "auteur VARCHAR(100) NOT NULL, " +
                "annee_publication INT, " +
                "disponible BOOLEAN DEFAULT TRUE) ENGINE=InnoDB");
            System.out.println(" Table 'livres' créée");
            
            // Table membres
            stmt.execute("CREATE TABLE IF NOT EXISTS membres (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nom VARCHAR(50) NOT NULL, " +
                "prenom VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "actif BOOLEAN DEFAULT TRUE) ENGINE=InnoDB");
            System.out.println(" Table 'membres' créée");
            
            // Table emprunts
            stmt.execute("CREATE TABLE IF NOT EXISTS emprunts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "livre_isbn VARCHAR(20), " +
                "membre_id INT, " +
                "date_emprunt DATE NOT NULL, " +
                "date_retour_prevue DATE NOT NULL, " +
                "date_retour_effective DATE, " +
                "FOREIGN KEY (livre_isbn) REFERENCES livres(isbn) ON DELETE CASCADE, " +
                "FOREIGN KEY (membre_id) REFERENCES membres(id) ON DELETE CASCADE) ENGINE=InnoDB");
            System.out.println(" Table 'emprunts' créée");
            
        } catch (SQLException e) {
            System.err.println("  Erreur création tables: " + e.getMessage());
        }
    }
    
    private void ajouterDonneesTest() {
        System.out.println(" Ajout données de test...");
        
        try (Statement stmt = connection.createStatement()) {
            
            // Vérifie si livres est vide
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM livres");
            if (rs.next() && rs.getInt("count") == 0) {
                // Ajoute des livres
                stmt.execute("INSERT INTO livres (isbn, titre, auteur, annee_publication, disponible) VALUES " +
                    "('978-2070360024', 'L''Étranger', 'Albert Camus', 1942, true), " +
                    "('978-2253006329', '1984', 'George Orwell', 1949, true), " +
                    "('978-2070360420', 'Le Petit Prince', 'Antoine de Saint-Exupéry', 1943, true), " +
                    "('978-2070413119', 'Harry Potter à l''école des sorciers', 'J.K. Rowling', 1997, true), " +
                    "('978-2253171560', 'Les Misérables', 'Victor Hugo', 1862, true)");
                System.out.println(" 5 livres ajoutés");
            }
            
            // Vérifie si membres est vide
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM membres");
            if (rs.next() && rs.getInt("count") == 0) {
                // Ajoute des membres
                stmt.execute("INSERT INTO membres (nom, prenom, email, actif) VALUES " +
                    "('Dupont', 'Jean', 'jean.dupont@email.com', true), " +
                    "('Martin', 'Marie', 'marie.martin@email.com', true), " +
                    "('Bernard', 'Pierre', 'pierre.bernard@email.com', true)");
                System.out.println(" 3 membres ajoutés");
            }
            
            // Vérifie si emprunts est vide
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM emprunts");
            if (rs.next() && rs.getInt("count") == 0) {
                // Ajoute des emprunts
                stmt.execute("INSERT INTO emprunts (livre_isbn, membre_id, date_emprunt, date_retour_prevue) VALUES " +
                    "('978-2070360024', 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY)), " +
                    "('978-2253006329', 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))");
                System.out.println("📖 2 emprunts ajoutés");
                
                // Met à jour disponibilité
                stmt.execute("UPDATE livres SET disponible = false WHERE isbn IN ('978-2070360024', '978-2253006329')");
            }
            
            System.out.println(" Données de test prêtes !");
            
        } catch (SQLException e) {
            System.err.println("  Erreur données test: " + e.getMessage());
        }
    }
    
    private void initialiserBaseDeDonnees() {
        creerTablesSiAbsentes();
        ajouterDonneesTest();
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
}
