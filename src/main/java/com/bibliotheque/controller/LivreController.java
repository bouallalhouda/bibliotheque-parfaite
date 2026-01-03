package com.bibliotheque.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.bibliotheque.service.BibliothequeService;
import com.bibliotheque.model.Livre;


public class LivreController {
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colISBN;
    
    @FXML private TextField txtTitre;
    @FXML private TextField txtAuteur;
    @FXML private TextField txtISBN;
    
    private BibliothequeService service;

    @FXML
    public void initialize() {
        // Initialise le service
        service = new BibliothequeService();
        // Configure les colonnes
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        // Charge les données
        chargerLivres();
    }

    public void setMainController(MainController main) {
        // liaison minimale pour FXML
    }
    private void chargerLivres() {
        ObservableList<Livre> livres = FXCollections.observableArrayList(
            service.getTousLesLivres()
        );
        tableLivres.setItems(livres);
    }

    @FXML
    private void handleAjouterLivre() {
        try {
            // Crée un nouveau livre depuis les champs
            Livre livre = new Livre();
            livre.setIsbn(txtISBN.getText());
            livre.setTitre(txtTitre.getText());
            livre.setAuteur(txtAuteur.getText());
            livre.setDisponible(true);
            
            // Appelle le SERVICE (pas le DAO directement !)
            service.ajouterLivre(livre);
            
            // Rafraîchit l'affichage
            chargerLivres();
            
            // Vide les champs
            clearFields();
            
            showSuccess("Livre ajouté avec succès !");
            
        } catch (IllegalArgumentException e) {
            showError("Erreur validation", e.getMessage());
        }
    }
    
    private void clearFields() {
        txtTitre.clear();
        txtAuteur.clear();
        txtISBN.clear();
    }
    
    private void showError(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}// Controller livres 
