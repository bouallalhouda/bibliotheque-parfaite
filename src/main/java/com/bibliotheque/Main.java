package com.bibliotheque;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Crée l'interface minimale
        TabPane tabPane = new TabPane();
        
        Tab tabLivres = new Tab("Livres");
        tabLivres.setContent(new VBox(new Label("Module Livres (Personne A)")));
        
        Tab tabMembres = new Tab("Membres");
        try {
            // Load the FXML for the members view
            java.net.URL res = Main.class.getResource("/fxml/MembreView.fxml");
            System.out.println("MembreView.fxml resource: " + res);
            VBox membresView = FXMLLoader.load(res);
            tabMembres.setContent(membresView);
        } catch (Exception e) {
            // fallback to placeholder if FXML fails
            tabMembres.setContent(new VBox(new Label("Module Membres (Personne B)")));
            System.err.println("Impossible de charger MembreView.fxml: " + e.getMessage());
        }
        
        Tab tabEmprunts = new Tab("Emprunts");
        tabEmprunts.setContent(new VBox(new Label("Module Emprunts (Personne C)")));
        
        tabPane.getTabs().addAll(tabLivres, tabMembres, tabEmprunts);
        
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Système de Gestion de Bibliothèque");
        // apply stylesheet if present
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ignored) {}
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("✅ Infrastructure Personne D - PRÊTE !");
        System.out.println("En attente des modules des autres...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}