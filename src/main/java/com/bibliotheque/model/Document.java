package com.bibliotheque.model;

public abstract class Document {
    private String id;
    private String titre;
    
    public abstract double calculerPenaliteRetard();
    
    // Getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
}