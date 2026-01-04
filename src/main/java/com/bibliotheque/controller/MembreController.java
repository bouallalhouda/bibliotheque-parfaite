package com.bibliotheque.controller;

import com.bibliotheque.model.Membre;
import com.bibliotheque.service.BibliothequeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MembreController {
	@FXML private TableView<Membre> tableMembres;
	@FXML private TableColumn<Membre, Integer> colId;
	@FXML private TableColumn<Membre, String> colNom;
	@FXML private TableColumn<Membre, String> colPrenom;
	@FXML private TableColumn<Membre, String> colEmail;
	@FXML private TableColumn<Membre, Boolean> colActif;

	@FXML private TextField nomField;
	@FXML private TextField prenomField;
	@FXML private TextField emailField;
	@FXML private Button ajouterButton;

	private BibliothequeService service;
	private ObservableList<Membre> membres = FXCollections.observableArrayList();

	public void setMainController(MainController main) {
		// liaison minimale pour FXML
	}

	@FXML
	public void initialize() {
		try {
			// Initialise le service
			service = new BibliothequeService();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'initialiser le service");
			return;
		}

		
		if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		if (colNom != null) colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		if (colPrenom != null) colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		if (colEmail != null) colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		if (colActif != null) colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

		loadMembres();
	}

	private void loadMembres() {
		membres.setAll(service.getTousLesMembres());
		if (tableMembres != null) tableMembres.setItems(membres);
	}

	@FXML
	private void ajouterMembre() {
		String nom = nomField.getText().trim();
		String prenom = prenomField.getText().trim();
		String email = emailField.getText().trim();

		if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Validation", "Remplissez tous les champs.");
			return;
		}

		if (!com.bibliotheque.util.StringValidator.isValidEmail(email)) {
			showAlert(Alert.AlertType.ERROR, "Validation", "Email invalide.");
			return;
		}

		Membre selected = tableMembres.getSelectionModel().getSelectedItem();
		try {
			if (selected != null && selected.getEmail().equals(email) && !selected.getNom().equals(nom)) {
				selected.setNom(nom);
				selected.setPrenom(prenom);
				selected.setEmail(email);
				service.mettreAJourMembre(selected);
				showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre mis à jour.");
			} else {
				Membre m = new Membre();
				m.setNom(nom);
				m.setPrenom(prenom);
				m.setEmail(email);
				m.setActif(true);
				service.ajouterMembre(m);
				showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre ajouté.");
			}
			loadMembres();
			nomField.clear();
			prenomField.clear();
			emailField.clear();
			tableMembres.getSelectionModel().clearSelection();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer le membre : " + e.getMessage());
		}
	}

	@FXML
	private void editerSelection() {
		Membre sel = tableMembres.getSelectionModel().getSelectedItem();
		if (sel == null) {
			showAlert(Alert.AlertType.WARNING, "Édition", "Sélectionnez un membre à éditer.");
			return;
		}
		nomField.setText(sel.getNom());
		prenomField.setText(sel.getPrenom());
		emailField.setText(sel.getEmail());
	}

	@FXML
	private void supprimerSelection() {
		Membre sel = tableMembres.getSelectionModel().getSelectedItem();
		if (sel == null) {
			showAlert(Alert.AlertType.WARNING, "Suppression", "Sélectionnez un membre à supprimer.");
			return;
		}
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer le membre sélectionné ?", ButtonType.YES, ButtonType.NO);
		confirm.setTitle("Confirmation");
		confirm.showAndWait().ifPresent(bt -> {
			if (bt == ButtonType.YES) {
				try {
					service.supprimerMembre(sel.getId());
					loadMembres();
					showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre supprimé.");
				} catch (Exception e) {
					e.printStackTrace();
					showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le membre : " + e.getMessage());
				}
			}
		});
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert a = new Alert(type);
		a.setTitle(title);
		a.setContentText(message);
		a.showAndWait();
	}
}
