package com.bibliotheque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Tes paramètres de connexion
    private String url =  "jdbc:mysql://127.0.0.1:3306/bibliotheque";
    private String username = "root";
    private String password = ""; // Ton mot de passe MySQL

    // Constructeur PRIVÉ (personne ne peut faire "new DatabaseConnection()")
    private DatabaseConnection() throws SQLException {
        try {
            // Charge le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Crée la connexion
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connexion MySQL établie !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable !");
            throw new SQLException("Driver MySQL manquant", e);
        }
    }

    // Méthode publique pour obtenir l'instance UNIQUE
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Retourne la connexion active
    public Connection getConnection() {
        return connection;
    }
    
    // Pour fermer la connexion (à appeler à la fermeture de l'app)
    public static void closeConnection() throws SQLException {
        if (instance != null && instance.connection != null) {
            instance.connection.close();
            instance = null;
            System.out.println("🔌 Connexion MySQL fermée.");
        }
    }
}
