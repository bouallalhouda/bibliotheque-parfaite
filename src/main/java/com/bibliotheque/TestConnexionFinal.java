package com.bibliotheque;

import java.sql.*;

public class TestConnexionFinal {
    public static void main(String[] args) {
        System.out.println("=== TEST DIRECT SANS SINGLETON ===\n");
        
        try {
            // Essaie de charger le driver
            System.out.println("1. Chargement du driver MySQL...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✅ Driver chargé");
            
            // Essaie plusieurs configurations
            String[] configurations = {
                "jdbc:mysql://localhost:3306/bibliotheque", "root", "",
                "jdbc:mysql://127.0.0.1:3306/bibliotheque", "root", "",
                "jdbc:mysql://localhost:3306/bibliotheque", "root", "root",
                "jdbc:mysql://127.0.0.1:3306/bibliotheque", "root", "root"
            };
            
            boolean success = false;
            
            for (int i = 0; i < configurations.length; i += 3) {
                String url = configurations[i];
                String user = configurations[i + 1];
                String pass = configurations[i + 2];
                
                System.out.println("\nEssai " + (i/3 + 1) + ":");
                System.out.println("   URL: " + url);
                System.out.println("   User: " + user);
                System.out.println("   Pass: " + (pass.isEmpty() ? "(vide)" : pass));
                
                try {
                    Connection conn = DriverManager.getConnection(url, user, pass);
                    System.out.println("   ✅ CONNEXION RÉUSSIE !");
                    
                    // Teste une requête simple
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT 1");
                    if (rs.next()) {
                        System.out.println("   ✅ Requête SQL fonctionne");
                    }
                    
                    // Ferme
                    stmt.close();
                    conn.close();
                    
                    success = true;
                    System.out.println("\n🎉 UTILISE CETTE CONFIGURATION :");
                    System.out.println("URL: " + url);
                    System.out.println("User: " + user);
                    System.out.println("Pass: " + (pass.isEmpty() ? "(vide)" : pass));
                    
                    break; // Arrête après premier succès
                    
                } catch (SQLException e) {
                    System.out.println("   ❌ Échec: " + e.getMessage());
                }
            }
            
            if (!success) {
                System.err.println("\n💥 TOUTES LES CONFIGURATIONS ONT ÉCHOUÉ !");
                System.err.println("Vérifie :");
                System.err.println("1. MySQL/MariaDB est démarré");
                System.err.println("2. La base 'bibliotheque' existe");
                System.err.println("3. Le driver est dans lib/");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ DRIVER INTROUVABLE !");
            System.err.println("Le fichier mysql-connector-j-9.5.0.jar doit être dans:");
            System.err.println("C:\\Users\\HP\\Downloads\\BibliothequeParfaite\\lib\\");
        }
    }
}