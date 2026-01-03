package com.bibliotheque;

import javafx.application.Application;
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
        tabMembres.setContent(new VBox(new Label("Module Membres (Personne B)")));
        
        Tab tabEmprunts = new Tab("Emprunts");
        tabEmprunts.setContent(new VBox(new Label("Module Emprunts (Personne C)")));
        
        tabPane.getTabs().addAll(tabLivres, tabMembres, tabEmprunts);
        
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Système de Gestion de Bibliothèque");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("✅ Infrastructure Personne D - PRÊTE !");
        System.out.println("En attente des modules des autres...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}