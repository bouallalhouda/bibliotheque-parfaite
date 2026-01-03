package com.bibliotheque.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import com.bibliotheque.model.Membre;
import com.bibliotheque.service.BibliothequeService;
import com.bibliotheque.dao.impl.MembreDAOImpl;

public class MembreController {

    private BibliothequeService service = new BibliothequeService(new MembreDAOImpl());

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private Button ajouterButton;

    @FXML
    private void ajouterMembre() {
        try {
            Membre m = new Membre(0, nomField.getText(), prenomField.getText(), emailField.getText(), true);
            service.ajouterMembre(m);
            System.out.println("Membre ajouté !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
// Controller membres 
