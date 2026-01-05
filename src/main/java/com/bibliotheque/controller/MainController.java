package com.bibliotheque.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * Contrôleur principal pour la navigation entre les onglets.
 * Gère le TabPane contenant les vues Livres, Membres et Emprunts.
 */
public class MainController {
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private LivreController livreController;
    
    @FXML
    private MembreController membreController;
    
    @FXML
    private EmpruntController empruntController;
    /**
     * Initialise le contrôleur principal.
     */
    @FXML
    public void initialize() {
        System.out.println("✅ MainController initialisé");
        
        // sous-contrôleurs si besoin
        if (livreController != null) {
            livreController.setMainController(this);
        }
        if (membreController != null) {
            membreController.setMainController(this);
        }
    }
}
