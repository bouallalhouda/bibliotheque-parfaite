package com.bibliotheque.model;

public class Membre extends Personne {
    private int id;
    private boolean actif;
    public Membre() {
        super("", "", "");
        this.id = 0;
        this.actif = true;
    }

    public Membre(int id, String nom, String prenom, String email, boolean actif) {
        super(nom, prenom, email); 
        this.id = id;
        this.actif = actif;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public boolean isActif() { return actif; }

    public void setActif(boolean actif) { this.actif = actif; }
}
