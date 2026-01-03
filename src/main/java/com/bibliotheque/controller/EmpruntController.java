package com.bibliotheque.controller;

import com.bibliotheque.exception.LimiteEmpruntDepasseeException;
import com.bibliotheque.exception.LivreIndisponibleException;
import com.bibliotheque.exception.MembreInactifException;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.service.EmpruntService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;

public class EmpruntController {

    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colLivreTitre;
    @FXML private TableColumn<Emprunt, String> colMembreNom;
    @FXML private TableColumn<Emprunt, String> colDateEmprunt;
    @FXML private TableColumn<Emprunt, String> colDateRetourPrevue;
    @FXML private TableColumn<Emprunt, String> colDateRetourEffective;

    @FXML private TextField isbnField;
    @FXML private TextField membreIdField;
    @FXML private Button emprunterButton;
    @FXML private Button retournerButton;
    @FXML private Label statusLabel;

    private final EmpruntService empruntService = new EmpruntService();
    private ObservableList<Emprunt> emprunts = FXCollections.observableArrayList();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public void setMainController(MainController main) {
        // liaison minimale pour FXML
    }

    @FXML
    public void initialize() {
        // Configure columns
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colLivreTitre != null) colLivreTitre.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getLivre() != null ? cellData.getValue().getLivre().getTitre() : ""
            ));
        if (colMembreNom != null) colMembreNom.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMembre() != null ? cellData.getValue().getMembre().getNom() : ""
            ));
        if (colDateEmprunt != null) colDateEmprunt.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateEmprunt() != null ? dateFormat.format(cellData.getValue().getDateEmprunt()) : ""
            ));
        if (colDateRetourPrevue != null) colDateRetourPrevue.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateRetourPrevue() != null ? dateFormat.format(cellData.getValue().getDateRetourPrevue()) : ""
            ));
        if (colDateRetourEffective != null) colDateRetourEffective.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateRetourEffective() != null ? dateFormat.format(cellData.getValue().getDateRetourEffective()) : ""
            ));

        loadEmprunts();
    }

    private void loadEmprunts() {
        emprunts.setAll(empruntService.getTousLesEmprunts());
        if (tableEmprunts != null) tableEmprunts.setItems(emprunts);
    }

    @FXML
    private void emprunterLivre() {
        try {
            String isbn = isbnField.getText().trim();
            String membreIdStr = membreIdField.getText().trim();

            if (isbn.isEmpty() || membreIdStr.isEmpty()) {
                showAlert("Erreur", "Veuillez saisir l'ISBN et l'ID du membre.");
                return;
            }

            int membreId = Integer.parseInt(membreIdStr);

            empruntService.emprunterLivre(isbn, membreId);
            statusLabel.setText("Livre emprunté avec succès!");
            loadEmprunts(); // Refresh table
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID du membre doit être un nombre.");
        } catch (LivreIndisponibleException | MembreInactifException | LimiteEmpruntDepasseeException e) {
            showAlert("Erreur", e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void retournerLivre() {
        Emprunt selected = tableEmprunts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un emprunt à retourner.");
            return;
        }

        try {
            empruntService.retournerLivre(selected.getId());
            statusLabel.setText("Livre retourné avec succès!");
            loadEmprunts(); // Refresh table

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du retour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        isbnField.clear();
        membreIdField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

