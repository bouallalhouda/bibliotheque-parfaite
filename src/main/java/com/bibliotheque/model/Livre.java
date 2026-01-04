package com.bibliotheque.model;

import javafx.beans.property.*;

public class Livre extends Document implements Empruntable {
    private String auteur;
    private int anneePublication;
    private String isbn;
    private boolean disponible;

    // Constructeur 
    public Livre() {}

    // Getters/Setters 
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public int getAnneePublication() { return anneePublication; }
    public void setAnneePublication(int anneePublication) { this.anneePublication = anneePublication; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    //Methodes pour liaison avec JAVAFX
    public StringProperty isbnProperty() { 
        return new SimpleStringProperty(this.isbn); 
    }
    
    public StringProperty titreProperty() { 
        return new SimpleStringProperty(this.getTitre()); 
    }
    
    public StringProperty auteurProperty() { 
        return new SimpleStringProperty(this.auteur); 
    }
    
    public IntegerProperty anneePublicationProperty() { 
        return new SimpleIntegerProperty(this.anneePublication); 
    }
    
    public BooleanProperty disponibleProperty() { 
        return new SimpleBooleanProperty(this.disponible); 
    }

    @Override
    public double calculerPenaliteRetard(int joursRetard) {
        return joursRetard * 0.50;
    }

    @Override
    public boolean peutEtreEmprunte() {
        return disponible;
    }

    @Override
    public void emprunter() {
        disponible = false;
    }

    @Override
    public void retourner() {
        disponible = true;
    }
}
