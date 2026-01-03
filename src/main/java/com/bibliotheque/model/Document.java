package com.bibliotheque.model;

public abstract class Document {
    private int id;
    private String titre;
    
    public abstract double calculerPenaliteRetard();
    
    // Getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
}