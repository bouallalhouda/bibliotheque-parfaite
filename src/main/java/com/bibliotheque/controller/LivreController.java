package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.BibliothequeService;
import model.Livre;
import java.sql.SQLException;

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
        try {
            // Initialise le service
            service = new BibliothequeService();
            
            // Configure les colonnes
            colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
            colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            
            // Charge les données
            chargerLivres();
            
        } catch (SQLException e) {
            showError("Erreur BD", e.getMessage());
        }
    }

    private void chargerLivres() {
        try {
            ObservableList<Livre> livres = FXCollections.observableArrayList(
                service.getTousLesLivres()
            );
            tableLivres.setItems(livres);
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les livres : " + e.getMessage());
        }
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
        } catch (SQLException e) {
            showError("Erreur BD", e.getMessage());
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
