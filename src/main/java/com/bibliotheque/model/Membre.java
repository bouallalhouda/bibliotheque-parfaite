package com.bibliotheque.model;
package model;

public class Membre extends Personne {
    private int id;
    private boolean actif;

    public Membre(int id, String nom, String prenom, String email, boolean actif) {
        super(nom, prenom, email); // appel du parent
        this.id = id;
        this.actif = actif;
    }

    public int getId() { return id; }

    public boolean isActif() { return actif; }

    public void setActif(boolean actif) { this.actif = actif; }
}
