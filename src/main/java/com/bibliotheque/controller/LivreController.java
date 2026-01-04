package com.bibliotheque.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.bibliotheque.service.BibliothequeService;
import com.bibliotheque.model.Livre;
import java.util.List;

public class LivreController {
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> colISBN;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, Boolean> colDisponible;
    
    @FXML private TextField txtTitre;
    @FXML private TextField txtAuteur;
    @FXML private TextField txtISBN;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtRecherche;  // ⬅️ AJOUTÉ
    
    private BibliothequeService service;

    @FXML
    public void initialize() {
        System.out.println("🎬 LivreController.initialize() DÉMARRÉ");
        
        try {
            // Initialise le service
            service = new BibliothequeService();
            System.out.println("✅ Service créé");
            
            // Configure TOUTES les colonnes
            colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
            colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));
            colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
            
            System.out.println("✅ Colonnes configurées");
            
            // Charge les données
            chargerLivres();
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR initialize: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur initialisation", "Erreur lors du chargement des livres: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void chargerLivres() {
        System.out.println("🔄 chargerLivres() appelé");
        
        try {
            List<Livre> livres = service.getTousLesLivres();
            System.out.println("✅ Service a retourné: " + livres.size() + " livres");
            
            // DEBUG: Affiche chaque livre
            for (Livre l : livres) {
                System.out.println("📖 " + l.getTitre() + " | " + l.getAuteur() + 
                                 " | ISBN: " + l.getIsbn() + 
                                 " | Année: " + l.getAnneePublication() +
                                 " | Disponible: " + l.isDisponible());
            }
            
            ObservableList<Livre> obsLivres = FXCollections.observableArrayList(livres);
            tableLivres.setItems(obsLivres);
            
            System.out.println("✅ TableView mise à jour avec " + obsLivres.size() + " livres");
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR chargerLivres: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur chargement", "Impossible de charger les livres: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAjouterLivre() {
        System.out.println("➕ handleAjouterLivre() appelé");
        
        try {
            // Validation des champs
            if (txtISBN.getText().isEmpty() || txtTitre.getText().isEmpty() || txtAuteur.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires", Alert.AlertType.WARNING);
                return;
            }
            
            // Crée un nouveau livre depuis les champs
            Livre livre = new Livre();
            livre.setIsbn(txtISBN.getText().trim());
            livre.setTitre(txtTitre.getText().trim());
            livre.setAuteur(txtAuteur.getText().trim());
            
            // Gestion de l'année (optionnelle)
            if (!txtAnnee.getText().isEmpty()) {
                try {
                    livre.setAnneePublication(Integer.parseInt(txtAnnee.getText().trim()));
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'année doit être un nombre", Alert.AlertType.WARNING);
                    return;
                }
            }
            
            livre.setDisponible(true);
            
            // Appelle le SERVICE
            service.ajouterLivre(livre);
            System.out.println("✅ Livre ajouté dans service: " + livre.getTitre());
            
            // Rafraîchit l'affichage
            chargerLivres();
            
            // Vide les champs
            clearFields();
            
            showAlert("Succès", "Livre ajouté avec succès !", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR handleAjouterLivre: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur validation", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleModifierLivre() {
        Livre livreSelectionne = tableLivres.getSelectionModel().getSelectedItem();
        if (livreSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un livre à modifier", Alert.AlertType.WARNING);
            return;
        }
        
        // Remplit les champs avec le livre sélectionné
        txtISBN.setText(livreSelectionne.getIsbn());
        txtTitre.setText(livreSelectionne.getTitre());
        txtAuteur.setText(livreSelectionne.getAuteur());
        txtAnnee.setText(String.valueOf(livreSelectionne.getAnneePublication()));
        
        showAlert("Information", "Livre sélectionné pour modification", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleSupprimerLivre() {
        Livre livreSelectionne = tableLivres.getSelectionModel().getSelectedItem();
        if (livreSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un livre à supprimer", Alert.AlertType.WARNING);
            return;
        }
        
        // Demande confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation suppression");
        alert.setHeaderText("Supprimer le livre");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer '" + livreSelectionne.getTitre() + "' ?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.supprimerLivre(livreSelectionne.getIsbn());
                System.out.println("✅ Livre supprimé: " + livreSelectionne.getTitre());
                chargerLivres();
                showAlert("Succès", "Livre supprimé avec succès !", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de supprimer le livre: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleRechercher() {
        String recherche = txtRecherche.getText().trim();
        System.out.println("🔍 Recherche: " + recherche);
        
        if (recherche.isEmpty()) {
            chargerLivres();
            return;
        }
        
        try {
            List<Livre> livres = service.rechercherLivres(recherche);
            ObservableList<Livre> obsLivres = FXCollections.observableArrayList(livres);
            tableLivres.setItems(obsLivres);
            
            System.out.println("✅ Recherche trouvée: " + livres.size() + " livres");
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR recherche: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de la recherche", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleRafraichir() {
        System.out.println("🔄 Rafraîchissement manuel");
        txtRecherche.clear();
        chargerLivres();
    }
    
    private void clearFields() {
        txtISBN.clear();
        txtTitre.clear();
        txtAuteur.clear();
        txtAnnee.clear();
    }
    
    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void setMainController(MainController main) {
        // Méthode pour liaison avec MainController si besoin
    }
}
