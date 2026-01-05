package com.bibliotheque;

import com.bibliotheque.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX.
 * Lance l'interface graphique du système de gestion de bibliothèque.
 */
public class Main extends Application {
    
    /**
     * Démarre l'application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialise la connexion à la base de données
            DatabaseConnection.getInstance();
            System.out.println(" Connexion BDD initialisée");
            
            // Charge le fichier FXML principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Crée la scène
            Scene scene = new Scene(root, 900, 600);
            
            // Configure et affiche la fenêtre
            primaryStage.setTitle("Système de Gestion de Bibliothèque");
            primaryStage.setScene(scene);
            
            // Ferme la connexion BDD à la fermeture de l'application
            primaryStage.setOnCloseRequest(e -> {
                try {
                    DatabaseConnection.getInstance().closeConnection();
                    System.out.println("✅ Connexion BDD fermée");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            
            primaryStage.show();
            System.out.println("✅ Application lancée avec succès !");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du lancement de l'application : " + e.getMessage());
        }
    }
    
    /**
     * Point d'entrée principal de l'application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
