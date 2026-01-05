package com.bibliotheque.controller;

import com.bibliotheque.exception.LimiteEmpruntDepasseeException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.MembreInactifException;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.service.BibliothequeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

/**
 * Contrôleur pour la gestion des emprunts de livres.
 * Permet d'ajouter des emprunts, de retourner des livres et de consulter l'historique.
 */
public class EmpruntController {
    
    // Formulaire d'emprunt
    @FXML private TextField txtISBN;
    @FXML private TextField txtIdMembre;
    
    // Formulaire de retour
    @FXML private TextField txtIdEmprunt;
    
    // TableView des emprunts
    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, Integer> colIdEmprunt;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colMembre;
    @FXML private TableColumn<Emprunt, String> colDateEmprunt;
    @FXML private TableColumn<Emprunt, String> colDateRetourPrevue;
    
    private BibliothequeService service;
    private ObservableList<Emprunt> emprunts = FXCollections.observableArrayList();
    
    /**
     * Initialise le contrôleur des emprunts.
     */
    @FXML
    public void initialize() {
        try {
            service = new BibliothequeService();
            
            // Configure les colonnes du tableau
            if (colIdEmprunt != null) colIdEmprunt.setCellValueFactory(new PropertyValueFactory<>("id"));
            if (colLivre != null) colLivre.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLivre().getTitre())
            );
            if (colMembre != null) colMembre.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMembre().getNom())
            );
            if (colDateEmprunt != null) colDateEmprunt.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateEmprunt().toString())
            );
            if (colDateRetourPrevue != null) colDateRetourPrevue.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateRetourPrevue().toString())
            );
            
            // Charge les emprunts en cours
            chargerEmprunts();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation : " + e.getMessage());
        }
    }
    
    /**
     * Charge les emprunts en cours dans le tableau.
     */
    private void chargerEmprunts() {
        emprunts.setAll(service.getEmpruntsEnCours());
        if (tableEmprunts != null) tableEmprunts.setItems(emprunts);
    }
    
    /**
     * Traite un nouvel emprunt de livre.
     */
    @FXML
    private void handleEmprunter() {
        String isbn = txtISBN.getText().trim();
        String idMembreStr = txtIdMembre.getText().trim();
        
        if (isbn.isEmpty() || idMembreStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez remplir tous les champs");
            return;
        }
        
        try {
            int idMembre = Integer.parseInt(idMembreStr);
            
            // Appelle le service pour effectuer l'emprunt
            Emprunt emprunt = service.emprunterLivre(isbn, idMembre);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                "Livre emprunté avec succès !\nRetour prévu : " + emprunt.getDateRetourPrevue());
            
            // Rafraîchit l'affichage
            chargerEmprunts();
            
            // Vide les champs
            txtISBN.clear();
            txtIdMembre.clear();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID membre doit être un nombre");
        } catch (LivreIndisponibleException e) {
            showAlert(Alert.AlertType.ERROR, "Livre indisponible", e.getMessage());
        } catch (MembreInactifException e) {
            showAlert(Alert.AlertType.ERROR, "Membre inactif", e.getMessage());
        } catch (LimiteEmpruntDepasseeException e) {
            showAlert(Alert.AlertType.ERROR, "Limite dépassée", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }
    
    /**
     * Traite le retour d'un livre emprunté.
     */
    @FXML
    private void handleRetourner() {
        String idEmpruntStr = txtIdEmprunt.getText().trim();
        
        if (idEmpruntStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez entrer l'ID de l'emprunt");
            return;
        }
        
        try {
            int idEmprunt = Integer.parseInt(idEmpruntStr);
            
            // Retourne le livre
            service.retournerLivre(idEmprunt);
            
            // Calcule la pénalité si applicable
            double penalite = service.calculerPenalite(idEmprunt);
            String message = "Livre retourné avec succès !";
            if (penalite > 0) {
                message += "\nPénalité : " + penalite + "€";
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", message);
            
            // Rafraîchit l'affichage
            chargerEmprunts();
            
            // Vide le champ
            txtIdEmprunt.clear();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID doit être un nombre");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
