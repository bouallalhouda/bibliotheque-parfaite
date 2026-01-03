package com.bibliotheque.model;

public class Livre extends Document {
    private int id;
    private String isbn;
    private String titre;
    private String auteur;
    private boolean disponible;

    public Livre() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
