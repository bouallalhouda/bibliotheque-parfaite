package com.bibliotheque.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private LivreController livreController;
    
    @FXML
    private MembreController membreController;
    
    @FXML
    private EmpruntController empruntController;
    
    public void initialize() {
        System.out.println("MainController initialisé");
        
        // Initialise les sous-contrôleurs si besoin
        if (livreController != null) {
            livreController.setMainController(this);
        }
    }
}