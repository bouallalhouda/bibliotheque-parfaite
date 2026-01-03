package com.bibliotheque.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.bibliotheque.model.Livre;
import com.bibliotheque.service.LivreService;

import java.io.IOException;
import java.time.LocalDate;

public class LivreController {
    
    // === SERVICES ===
    private LivreService livreService;
    private MainController mainController;
    
    // === LISTE OBSERVABLE ===
    private ObservableList<Livre> livresList;
    
    // === COMPOSANTS FXML ===
    @FXML private TableView<Livre> tableViewLivres;
    @FXML private TableColumn<Livre, Integer> colId;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, String> colCategorie;
    @FXML private TableColumn<Livre, Integer> colQuantiteTotal;
    @FXML private TableColumn<Livre, Integer> colQuantiteDispo;
    @FXML private TableColumn<Livre, String> colEtat;
    
    @FXML private TextField txtRecherche;
    @FXML private Button btnRechercher;
    @FXML private Button btnRechercheAvancee;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnRafraichir;
    @FXML private Label lblStatistiques;
    
    // === INITIALISATION ===
    @FXML
    public void initialize() {
        // Initialiser le service
        livreService = new LivreService();
        livresList = FXCollections.observableArrayList();
        
        // Configurer les colonnes
        configurerColonnes();
        
        // Charger les données
        chargerLivres();
        
        // Configurer les listeners
        configurerListeners();
        
        // Mettre à jour les statistiques
        mettreAJourStatistiques();
    }
    
    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colAnnee.setCellValueFactory(cellData -> {
            Livre livre = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> 
                livre.getDatePublication() != null ? livre.getDatePublication().getYear() : null);
        });
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colQuantiteTotal.setCellValueFactory(new PropertyValueFactory<>("quantiteTotale"));
        colQuantiteDispo.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
        
        // Colonne état calculée
        colEtat.setCellValueFactory(cellData -> {
            Livre livre = cellData.getValue();
            String etat = livre.isDisponible() ? "✅ Disponible" : "❌ Indisponible";
            return javafx.beans.binding.Bindings.createStringBinding(() -> etat);
        });
        
        // Personnaliser l'affichage de l'état
        colEtat.setCellFactory(column -> new TableCell<Livre, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("✅")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
    private void configurerListeners() {
        // Recherche en temps réel
        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2 || newValue.isEmpty()) {
                rechercherLivres();
            }
        });
        
        // Double-clic sur une ligne pour modifier
        tableViewLivres.setRowFactory(tv -> {
            TableRow<Livre> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modifierLivre();
                }
            });
            return row;
        });
        
        // Listener pour la sélection
        tableViewLivres.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                btnModifier.setDisable(newSelection == null);
                btnSupprimer.setDisable(newSelection == null);
            });
    }
    
    // === MÉTHODES D'ACTION ===
    @FXML
    private void chargerLivres() {
        livresList.clear();
        livresList.addAll(livreService.getAllLivres());
        tableViewLivres.setItems(livresList);
        mettreAJourStatistiques();
    }
    
    @FXML
    private void rechercherLivres() {
        String motCle = txtRecherche.getText().trim();
        
        if (motCle.isEmpty()) {
            chargerLivres();
        } else {
            livresList.clear();
            livresList.addAll(livreService.rechercherGlobal(motCle));
            tableViewLivres.setItems(livresList);
        }
        
        mettreAJourStatistiques();
    }
    
    @FXML
    private void ouvrirRechercheAvancee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/RechercheView.fxml"));
            Parent root = loader.load();
            
            RechercheController controller = loader.getController();
            controller.setLivreController(this);
            controller.setCategories(livreService.getCategories());
            
            Stage stage = new Stage();
            stage.setTitle("Recherche avancée");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la recherche avancée", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void ajouterLivre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/LivreForm.fxml"));
            Parent root = loader.load();
            
            LivreFormController controller = loader.getController();
            controller.setLivreController(this);
            controller.setModeAjout();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un nouveau livre");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void modifierLivre() {
        Livre livreSelectionne = tableViewLivres.getSelectionModel().getSelectedItem();
        if (livreSelectionne == null) {
            showAlert("Avertissement", "Veuillez sélectionner un livre à modifier", 
                     Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/LivreForm.fxml"));
            Parent root = loader.load();
            
            LivreFormController controller = loader.getController();
            controller.setLivreController(this);
            controller.setModeModification(livreSelectionne);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier le livre : " + livreSelectionne.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void supprimerLivre() {
        Livre livreSelectionne = tableViewLivres.getSelectionModel().getSelectedItem();
        if (livreSelectionne == null) {
            showAlert("Avertissement", "Veuillez sélectionner un livre à supprimer", 
                     Alert.AlertType.WARNING);
            return;
        }
        
        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le livre : " + livreSelectionne.getTitre());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce livre ? Cette action est irréversible.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = livreService.supprimerLivre(livreSelectionne.getId());
                    if (success) {
                        showAlert("Succès", "Livre supprimé avec succès", Alert.AlertType.INFORMATION);
                        chargerLivres();
                    } else {
                        showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
                    }
                } catch (IllegalStateException e) {
                    showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    @FXML
    private void rafraichir() {
        chargerLivres();
        txtRecherche.clear();
        showAlert("Information", "Liste rafraîchie", Alert.AlertType.INFORMATION);
    }
    
    // === MÉTHODES UTILITAIRES ===
    public void appliquerRechercheAvancee(String titre, String auteur, String categorie, 
                                         String isbn, Integer anneeMin, Integer anneeMax) {
        livresList.clear();
        livresList.addAll(livreService.rechercherAvancee(titre, auteur, categorie, 
                                                         isbn, anneeMin, anneeMax));
        tableViewLivres.setItems(livresList);
        mettreAJourStatistiques();
    }
    
    private void mettreAJourStatistiques() {
        int total = livresList.size();
        long disponibles = livresList.stream().filter(Livre::isDisponible).count();
        
        lblStatistiques.setText(String.format("📊 %d livre(s) - %d disponible(s)", total, disponibles));
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Pour le rafraîchissement après ajout/modification
    public void refreshTableView() {
        chargerLivres();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
